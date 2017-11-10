package ru.track.prefork;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicLong;

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
        ConcurrentMap<Long, User> users = new ConcurrentHashMap<>();
        AtomicLong counter = new AtomicLong(0);
        Socket socket;
        ServerSocket server;
        String name;

        final ThreadPoolExecutor pool = new ThreadPoolExecutor(10,10,
                0L, TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<Runnable>(2));
        pool.setRejectedExecutionHandler(new ThreadPoolExecutor.AbortPolicy());

        //ExecutorService  service = Executors.newCachedThreadPool();

        server = new ServerSocket(port);
        System.out.println("server started");

        Thread master = new Thread(new Adminka(users));
        master.start();

        try {
            while (true) {
                socket = server.accept();
                System.out.println("Connection accepted: " + socket);
                Long id = counter.incrementAndGet();
                name = "Client[" + id + "]";
                User user = new User(name, socket,id);
                users.put(id, user);
                try {
                    pool.submit(new AloneThread(user, users));
//                    service.submit(new AloneThread(user, users));
                } catch (Exception e) {
                    e.printStackTrace();
                    users.remove(id);
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





