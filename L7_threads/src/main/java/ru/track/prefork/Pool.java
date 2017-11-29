package ru.track.prefork;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;

public class Pool {
    private static Logger logger = LoggerFactory.getLogger("logger");
    private ArrayList<Connection> connections = new ArrayList<>();

    private synchronized void serveClient(Connection connection) throws IOException {
        logger.info("connected");

        byte[] bytes = new byte[1024]; // TODO: clear constant here

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
        logger.info("connection lost with");
    }

    private synchronized void broadcast(Connection currentConnection, byte[] message, int size) throws IOException {
        for (Connection connection : connections) {
            if (!connection.equals(currentConnection)) {
                Socket socket = connection.getSocket();
                OutputStream outputStream = socket.getOutputStream();
                outputStream.write(message, 0, size);
                outputStream.flush();
            }
        }
    }

    public void addClient(int id, String host, int port, Socket socket) throws IOException {
        Connection connection = new Connection(id, host, port, socket);
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