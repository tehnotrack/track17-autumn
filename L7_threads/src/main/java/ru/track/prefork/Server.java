package ru.track.prefork;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 *
 */
public class Server {
    private int port;

    public Server(int port) {
        this.port = port;
    }

    public void serve() throws IOException {
        ServerSocket serverSocket = new ServerSocket(port);
        Socket socket;
        Pool pool = new Pool();

        while (true) {
            socket = serverSocket.accept();

            pool.addClient(socket);
        }
    }

    public static void main(String[] args) {
        try {
            Server server = new Server(8000);
            server.serve();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
