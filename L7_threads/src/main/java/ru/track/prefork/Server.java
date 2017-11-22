package ru.track.prefork;

import org.slf4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;


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
        int socketNum = 0;
        while (!serverSocket.isClosed()) {
            Socket clientSocket = serverSocket.accept();

            Runnable worker = (new Worker()).init(clientSocket, socketNum++);

            Thread thread = new Thread(worker);
            thread.setName("Client[" + socketNum + "]" + clientSocket.getInetAddress() + ":" + clientSocket.getPort());
            thread.start();
            log.info("New client accepted: " + thread.getName());
        }
    }

    class Worker implements Runnable {
        Socket socket;
        int socketId; // connection counter

        @Override
        public void run() {
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

        public Runnable init(Socket socket, int socketId) {
            this.socket = socket;
            this.socketId = socketId;
            return (this);
        }
    }
}
