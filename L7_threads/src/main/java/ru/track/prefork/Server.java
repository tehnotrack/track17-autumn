package ru.track.prefork;

import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;


/**
 *
 */
public class Server {
    private static final AtomicInteger id = new AtomicInteger(0);
    private int port;
    static Logger log = LoggerFactory.getLogger(Server.class);
    ExecutorService ex;

    public Server(int port) {
        this.port = port;
        ex = Executors.newCachedThreadPool();
    }

    public void serve() {
        try (
                ServerSocket listener = new ServerSocket(port, 10, InetAddress.getByName("localhost"))) {
            while (true) {
                Socket clientSocket;
                try {
                    clientSocket = listener.accept();
                } catch (IOException e) {
                    e.printStackTrace();
                    continue;
                }
                ClientHandler ch = new ClientHandler(clientSocket, id.getAndIncrement());
                ex.execute(ch);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        Server server = new Server(9876);
        server.serve();
    }
}