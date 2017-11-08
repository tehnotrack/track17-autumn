package ru.track.prefork;


import javax.xml.bind.Element;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicLong;


/**
 *
 */
public class Server {
    private int port;
    private boolean isRunning = false;
    private ServerSocket socket;
    private AtomicLong counter = new AtomicLong(0);
    private ExecutorService pool = null;
    private ConcurrentHashMap<Long, Thread> threadMap;


    private Server(int port, ExecutorService pool) {
        this.port = port;
        this.isRunning = true;
        this.pool = pool;
        this.threadMap = new ConcurrentHashMap();
        if(startServer() == 0) {
            listenLoop();
        }
    }

    private int startServer() {
        try {
            System.out.println("[main]: Try to start server.");
            this.socket = new ServerSocket(this.port);
            System.out.println("[main]: Server started at port " + this.port + ".");
        } catch (IOException e) {
            System.out.println("[main]: Can't start socket. Error: " + e.getLocalizedMessage());
            return(-1);
        } catch (NullPointerException e) {
            System.out.println("[main]: Can't start socket. Error: " + e.getLocalizedMessage());
        }
        return 0;
    }

    private void listenLoop() {
        while (isRunning) {
            pool.execute(new Handler(this.socket));
        }
    }

    public class Handler implements Runnable {

        private ServerSocket socket;
        private DataInputStream in = null;
        private DataOutputStream out = null;

        private Handler(ServerSocket socket) {
            this.socket = socket;
        }

        @Override
        public void run() {

            try {
                String oldName = Thread.currentThread().getName();
                System.out.println(String.format("[%s]: Wait for connection.", Thread.currentThread().getName()));
                Socket client = this.socket.accept();

                Thread.currentThread().setName(String.format("Client[%d]@[%s]:[%d]", counter.incrementAndGet(),
                        client.getInetAddress().toString(),
                        client.getPort()));


                System.out.println(String.format("[%s]: Client accepted.", Thread.currentThread().getName()));

                this.out = new DataOutputStream(client.getOutputStream());
                this.in = new DataInputStream(client.getInputStream());

                threadMap.put(counter.get(), Thread.currentThread());

                while(!client.isClosed()) {
                    String data = in.readUTF();
                    System.out.println(String.format("[%s]: Client write: " + data + ".",
                            Thread.currentThread().getName()));
                    if(data.equals("exit")) {
                        break;
                    }

                    out.writeUTF(data); //# HELO Server;

//                    for(Map.Entry<Long, Thread> entry : threadMap.entrySet()) {
//                        entry.getValue();
//                    }

                    out.flush();
                    System.out.println(String.format("[%s]: Sent: \"" + data + "\" to client.",
                            Thread.currentThread().getName()));

                }
                out.close();
                in.close();
                client.close();
                System.out.println(String.format("[%s]: Close connection.",
                        Thread.currentThread().getName()));
                Thread.currentThread().setName(oldName);
                threadMap.remove(counter.get());
            } catch (IOException e) {
                System.out.println(String.format("[%s]: Error while new client acceptance.",
                        Thread.currentThread().getName()));
            }
        }

        public void sendMessage(String message) {
            try {
                this.out.writeUTF(message);
                this.out.flush();
            } catch (IOException e) {
                System.out.println(String.format("[%s]: Error while sending message.",
                        Thread.currentThread().getName()));
            }

        }

        @Override
        public String toString(){
            return Thread.currentThread().getName();
        }
    }


    public static void main(String[] args) throws Throwable {
        ThreadPoolExecutor pool = new ThreadPoolExecutor(10, 10, 0L,
                TimeUnit.MILLISECONDS, new LinkedBlockingQueue(2));

        pool.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());

        Server server = new Server(8080, pool);
    }
}


