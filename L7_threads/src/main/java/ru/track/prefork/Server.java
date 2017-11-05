package ru.track.prefork;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Map;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 *
 */
public class Server {

    public static void main(String[] args) throws IOException {

        BlockingQueue<String> drop = new SynchronousQueue<String>();
        Map<String, Socket> map = new ConcurrentHashMap<>();

        AtomicInteger socCount = new AtomicInteger(0);
        Socket socket;
        ServerSocket server;
        String name;
        ExecutorService service;

        server = new ServerSocket(8100);
        service = Executors.newCachedThreadPool();
        System.out.println("server started");
        try {
            while (true) {
                socket = server.accept();
                name = "Client@"  + socket.getInetAddress() +":" + socket.getPort();
                map.put(name,socket);
                System.out.println("Connection accepted: " + socket);
                try {
                    service.submit(new AloneThread(socket, map));

                } catch (Exception e) {
                    e.printStackTrace();
                    map.values().remove(socket);
                    socket.close();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            System.out.println("closing...");
            server.close();
        }
    }


}




