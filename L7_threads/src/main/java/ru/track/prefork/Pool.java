package ru.track.prefork;

import com.google.gson.Gson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.track.prefork.database.Database;
import ru.track.prefork.database.exceptions.InvalidAuthor;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

public class Pool {
    private static final int MAX_SIZE = 1024;
    private static Logger logger = LoggerFactory.getLogger("logger");

    private AtomicInteger id = new AtomicInteger();
    private Set<ServerConnection> serverConnections = Collections.synchronizedSet(new HashSet<>());

    private void serveClient(ServerConnection serverConnection) throws IOException {
        logger.info("connected");

        byte[] bytes = new byte[MAX_SIZE];

        Socket socket = serverConnection.getSocket();

        InputStream inputStream = socket.getInputStream();

        int messageSize;
        while ((messageSize = inputStream.read(bytes)) != -1) {
            String text = new String(bytes, 0, messageSize);

            if (text.equals("exit")) {
                break;
            }

            Gson gson = new Gson();
            Message message = gson.fromJson(text, Message.class);

            logger.info("new message from " + message.getUsername() + ": " + message.getText());

            // saveMessage(message); // TODO: save messages after the server is ready

            broadcast(serverConnection, bytes, messageSize);

            logger.info("sent messages");

            Arrays.fill(bytes, (byte) 0);
        }

        socket.close();
        serverConnections.remove(serverConnection);

        logger.info("connection lost");
    }

    private void saveMessage(String message) {
        String author = "luthor";

        try {
            Connection connection = Database.getConnection(author);

            Database.save(connection, message, author);
        } catch (InvalidAuthor | SQLException e) {
            e.printStackTrace();
        }
    }

    private void broadcast(ServerConnection currentServerConnection, byte[] message, int size) throws IOException {
        for (ServerConnection serverConnection : serverConnections) {
            if (!serverConnection.equals(currentServerConnection)) {
                Socket socket = serverConnection.getSocket();
                OutputStream outputStream = socket.getOutputStream();
                outputStream.write(message, 0, size);
                outputStream.flush();
            }
        }
    }

    public void addClient(Socket socket) throws IOException {
        ServerConnection serverConnection = new ServerConnection(id.getAndIncrement(), socket.getLocalAddress().toString(), socket.getPort(), socket);
        serverConnections.add(serverConnection);

        Thread thread = new Thread(() -> {
            try {
                serveClient(serverConnection);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        thread.setName(serverConnection.getClientInfo());
        thread.start();
    }
}