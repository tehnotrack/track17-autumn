package ru.track.prefork;

import org.slf4j.Logger;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Arrays;


public class Server {
    static Logger log = org.slf4j.LoggerFactory.getLogger(Server.class);
    private int port;

    public Server(int port) {
        this.port = port;
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
        while (true) {
            // new client
            Socket socket = serverSocket.accept();
            log.info("Accepted: " + socket.getPort());

            // streams
            OutputStream out = socket.getOutputStream();
            InputStream in = socket.getInputStream();

            while (true) {
                // read from client socket
                byte[] buffer = new byte[1024];
                int nRead = in.read(buffer);
                if (nRead < 1) {
                    break;
                }

                // write echo to client
                out.write(buffer, 0, nRead);
                out.flush();

                // print msg from client
                byte[] slicedBuffer = Arrays.copyOfRange(buffer, 0, nRead);
                String msgFromClient = new String(slicedBuffer);
                System.out.println("Client" + socket.getPort() + ":" + msgFromClient);

                // condition to close connection
                if (msgFromClient.equals("exit")) {
                    break;
                }
            }
            socket.close();
            log.info("Disconnected: " + socket.getPort());
        }
    }
}
