package ru.track.prefork;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;
import java.util.concurrent.*;

/**
 *
 */
public class Server {

    private int port;

    Server (int port) {
        this.port = port;
    }

    public static void main(String[] args) throws IOException {

        Server server = new Server(8100);
        server.starting();
    }

    public void starting () throws IOException {
        Map<String, Socket> map = new ConcurrentHashMap<>();
        List<User> users = new ArrayList<>();
        Socket socket;
        ServerSocket server;
        String name;
        ExecutorService service;
        server = new ServerSocket(port);
        service = Executors.newCachedThreadPool();
        System.out.println("server started");

        try {
            while (true) {
                socket = server.accept();
                System.out.println("Connection accepted: " + socket);
                name = "Client@"  + socket.getInetAddress() +":" + socket.getPort();
                User user = new User(name, socket);
                users.add(user);
                try {
                    service.submit(new AloneThread(user, users));
                } catch (Exception e) {
                    e.printStackTrace();
                    map.values().remove(socket);
                    socket.close();
                }
            }
        } catch (IOException e) {
            System.out.println("couldn't add user");
        } finally {
            System.out.println("closing...");
            server.close();
        }
    }
}





