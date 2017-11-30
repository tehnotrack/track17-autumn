package ru.track.prefork;

import sun.misc.IOUtils;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicLong;

/**
 *
 */
public class Server {
    private int port;

    ConcurrentHashMap<Long, String> mmap;

    public Server(int port) {
        this.port = port;
        mmap  = new ConcurrentHashMap<>();
    }

    public static String getName (long num, Socket sock) {
        return String.format("Client[%d]@[%s]:[%d]",
                num,
                sock.getInetAddress().getHostName(),
                sock.getPort());
    }

    public void proceccSocket(final Socket client, long id) {
        try (
                PrintWriter out = new PrintWriter(client.getOutputStream(), true);
                BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()));
        ) {
            mmap.put(id, getName(id, client));

            System.out.println("Connected: " + mmap.get(id));

            String inpStr;
            while (null != (inpStr = in.readLine())) {
                if (inpStr.equalsIgnoreCase("exit")) {
                    client.close();
                    break;
                }
                out.println(inpStr);
            }

        } catch (java.io.IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        Server server = new Server(5000);
        AtomicLong counter = new AtomicLong(1);
        ExecutorService pool = Executors.newFixedThreadPool(20);

        try (ServerSocket serverSocket = new ServerSocket(5000)) {
            Socket socket;
            while (true) {
                socket = serverSocket.accept();
                final Socket client = socket;
                pool.submit(new Thread(() -> {
                    Thread.currentThread().setName(getName(counter.get(), client));
                    server.proceccSocket(client, counter.getAndIncrement());
                }));
            }
        }     catch(java.io.IOException e){
                e.printStackTrace();
        }
    }
}
