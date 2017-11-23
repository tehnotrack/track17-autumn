package ru.track.prefork;

import com.sun.mail.iap.Protocol;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.ProtocolException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;


public class Server {
    static Logger log = LoggerFactory.getLogger(Server.class);

    private int port;
    private AtomicLong serverCounter = new AtomicLong(0);
//    private Protocol<String> protocol;

    public Server(int port) {
        this.port = port;
//        this.protocol = protocol;
    }

    public static void main(String[] args) {
        Server server = new Server(9000);
        try {
            server.serve();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void serve() throws Exception {
        System.out.println("Server started!");

        ServerSocket serverSocket = new ServerSocket(port, 10, InetAddress.getByName("localhost"));
        while (!serverSocket.isClosed()) {
            final Socket socket = serverSocket.accept();

            Thread thread = new Thread(() -> {
                try {
                    log.info("Connected");
                    handleSocket(socket);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            });
            thread.setName(String.format("Client[%d]@%s:%d", serverCounter.getAndIncrement(), socket.getInetAddress(), socket.getPort()));
            thread.start();
        }
    }

    private void handleSocket(Socket socket) throws IOException, ProtocolException {
        try (OutputStream out = socket.getOutputStream();
             InputStream in = socket.getInputStream();) {

            while (!socket.isClosed()) {
                // read from client socket
                byte[] buffer = new byte[1024];
                int nbytes = in.read(buffer);
                if (nbytes < 1) {
                    break;
                }

                // write echo to client
                out.write(buffer, 0, nbytes);
                out.flush();

                // print msg from client
                String msgFromClient = new String(buffer, 0, nbytes);
                log.info("Message: " + msgFromClient);

                // condition to close connection
                if (msgFromClient.equals("exit")) {
                    break;
                }
            }
        } catch (IOException e) {
            log.error("IOException", e);
        } finally {
            try {
                socket.close();
            } catch (IOException e) {
                log.error("Can't close client socket. " + e);
            }
            log.info("Client disconnected");
        }
    }
}
