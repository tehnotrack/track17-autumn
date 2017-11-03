package ru.track.prefork;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Arrays;

/**
 *
 */
public class Server {
    private static Logger logger = LoggerFactory.getLogger("logger");
    private int port;
    private static final int MAX_COUNT = 10;

    public Server(int port) {
        this.port = port;
    }

    private void serve() throws IOException {
        ServerSocket serverSocket = new ServerSocket(port);
        Socket socket;

        byte[] bytes = new byte[MAX_COUNT];

        while (true) {
            socket = serverSocket.accept();

            logger.info("client connected");

            OutputStream outputStream = socket.getOutputStream();
            InputStream inputStream = socket.getInputStream();

            int messageSize;
            while ((messageSize = inputStream.read(bytes)) != -1) {
                String message = new String(bytes, 0, messageSize);

                if (message.equals("exit")) {
                    break;
                }

                logger.info("new message from client: " + message);

                outputStream.write(bytes, 0, messageSize);
                outputStream.flush();

                logger.info("sent message to client");

                Arrays.fill(bytes, (byte) 0);
            }

            socket.close();

            logger.info("connection lost");
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
