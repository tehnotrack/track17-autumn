package ru.track.prefork;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;

/**
 *
 */
public class Server {
    private static Logger logger = LoggerFactory.getLogger("logger");

    private int port;
    private int clientId = 0;
    private static final int MAX_COUNT = 1024;

    public Server(int port) {
        this.port = port;
    }

    private class NewClient implements Runnable {
        private Socket socket;
        private String client;

        NewClient(Socket socket, String client) {
            this.socket = socket;
            this.client = client;
        }

        @Override
        public void run() {
            try {
                connectClient(socket, client);
            } catch (IOException e) {
                e.printStackTrace(); // TODO: think about error handling
            }
        }
    }

    private void connectClient(Socket socket, String client) throws IOException {
        byte[] bytes = new byte[MAX_COUNT];

        OutputStream outputStream = socket.getOutputStream();
        InputStream inputStream = socket.getInputStream();

        int messageSize;
        while ((messageSize = inputStream.read(bytes)) != -1) {
            String message = new String(bytes, 0, messageSize);

            if (message.equals("exit")) {
                break;
            }

            logger.info("new message from " + client + ": " + message);

            outputStream.write(bytes, 0, messageSize);
            outputStream.flush();

            logger.info("sent message to " + client);

            Arrays.fill(bytes, (byte) 0);
        }

        socket.close();
        logger.info("connection lost with " + client);
    }

    public void serve() throws IOException {
        ServerSocket serverSocket = new ServerSocket(port);
        Socket socket;

        while (true) {
            socket = serverSocket.accept();

            String client = "Client[" + clientId++ + "]@" + socket.getLocalAddress() + ":" + socket.getPort();

            logger.info(client + " connected");

            Thread newClient = new Thread(new NewClient(socket, client));
            newClient.setName(client);
            newClient.start();
        }
    }

    public static void main(String[] args) {
        try {
            Server server = new Server(8080);
            server.serve();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
