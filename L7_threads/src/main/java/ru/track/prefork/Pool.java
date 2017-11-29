package ru.track.prefork;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.*;

public class Pool {
    private static final int MAX_SIZE = 1024;
    private static Logger logger = LoggerFactory.getLogger("logger");
//    private ArrayList<Connection> connections = new ArrayList<>();
    private Set<Connection> connections = Collections.synchronizedSet(new HashSet<>());

    private void serveClient(Connection connection) throws IOException {
        logger.info("connected");

        byte[] bytes = new byte[MAX_SIZE];

        Socket socket = connection.getSocket();

        InputStream inputStream = socket.getInputStream();

        int messageSize;
        while ((messageSize = inputStream.read(bytes)) != -1) {
            String message = new String(bytes, 0, messageSize);

            if (message.equals("exit")) {
                break;
            }

            logger.info("new message: " + message);

            broadcast(connection, bytes, messageSize);

            logger.info("sent messages");

            Arrays.fill(bytes, (byte) 0);
        }

        socket.close();
        connections.remove(connection);

        logger.info("connection lost");
    }

    private void broadcast(Connection currentConnection, byte[] message, int size) throws IOException {
        for (Connection connection : connections) {
            if (!connection.equals(currentConnection)) {
                Socket socket = connection.getSocket();
                OutputStream outputStream = socket.getOutputStream();
                outputStream.write(message, 0, size);
                outputStream.flush();
            }
        }
    }

    public void addClient(int id, Socket socket) throws IOException {
        Connection connection = new Connection(id, socket.getLocalAddress().toString(), socket.getPort(), socket);
        connections.add(connection);

        Thread thread = new Thread(() -> {
            try {
                serveClient(connection);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        thread.setName(connection.getClientInfo());
        thread.start();
    }
}