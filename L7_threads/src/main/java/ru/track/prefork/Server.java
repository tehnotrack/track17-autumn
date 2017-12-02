package ru.track.prefork;

import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.BiConsumer;


/**
 *
 */
public class Server {

    private final AtomicLong id = new AtomicLong(0);
    private int port;
    static Logger log = LoggerFactory.getLogger(Server.class);
    ExecutorService ex;
    private ConcurrentHashMap<Long, ClientHandler> clients;

    public Server(int port) {
        this.port = port;
        ex = Executors.newCachedThreadPool();
        clients = new ConcurrentHashMap<>();
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
                clients.put(ch.getId(), ch);
                ex.execute(ch);
                ch.broadcast(clients, "asdv");
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