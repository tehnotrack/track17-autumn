package ru.track.prefork;

import com.sun.org.apache.bcel.internal.generic.INSTANCEOF;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

/**
 *
 */
public class Server {
    private static Logger logger = LoggerFactory.getLogger("logger");
    private int port;
    private static final int MAX_COUNT = 1024;

    public Server(int port) {
        this.port = port;
    }

    private void serve() throws IOException {
        ServerSocket serverSocket = new ServerSocket(port);
        Socket socket = serverSocket.accept();

        logger.info("new client connected");

        byte[] bytes = new byte[MAX_COUNT];

        while (true)
        {
            if (socket.isClosed()) {
                socket = serverSocket.accept();

                logger.info("new client connected");
            }

            OutputStream outputStream = socket.getOutputStream();
            InputStream inputStream = socket.getInputStream();

            int readBytesCount = inputStream.read(bytes);
            if (readBytesCount == -1) {
                socket.close();
            }
            logger.info("read " + readBytesCount + " bytes from client");

            outputStream.write(bytes);
            logger.info("written bytes to client");

            if (!socket.isBound()) {
                socket.close();

                logger.info("connection with client closed");
            }
        }
    }

    public static void main(String[] args) {
        Server server = null;
        try {
            server = new Server(8080);
            server.serve();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
