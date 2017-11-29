package ru.track.prefork;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Map;
import java.util.Scanner;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicLong;


/**
 *
 */
public class Server {
    static Logger log = LoggerFactory.getLogger(Server.class);
    private int port;
    private AtomicLong counter;
    private ConcurrentMap<Long, Worker> workersMap;

    public Server(int port) {
        this.port = port;
        counter = new AtomicLong(0);
        workersMap = new ConcurrentHashMap<>();
    }

    private void logexception(Exception e){
        log.error(e.toString());
        for (StackTraceElement elem : e.getStackTrace())
            log.error("at " + elem.toString());
    }

    public void serve() {
        ServerSocket ssock = null;
        try {
            ssock = new ServerSocket(port, 10, InetAddress.getByName("localhost"));
            Socket sock;
            Worker worker;
            new Thread(() -> admin()).start();
            while (true) {
                sock = ssock.accept();
                try {
                    worker = new Worker(sock);
                    worker.start();
                } catch (IOException e) {
                    log.error("Connection failed");
                    logexception(e);
                }
            }
        } catch (Exception e) {
            log.error("Server dropped");
            logexception(e);
        } finally {
            IOUtils.closeQuietly(ssock);
        }
    }

    public void admin() {
        try (Scanner in = new Scanner(System.in)) {
            String cmd;
            Long id;
            while (true) {
                cmd = in.nextLine();
                if (cmd.equals("list"))
                    for (Map.Entry<Long, Worker> entry : workersMap.entrySet())
                        System.out.println(entry.getValue().name);
                else {
                    String[] strlist = cmd.split(" ");
                    if (strlist.length == 2 && strlist[0].equals("drop"))
                        try {
                            id = new Long(strlist[1]);
                            if (workersMap.get(id) == null)
                                System.out.println("No such client");
                            else
                                IOUtils.closeQuietly(workersMap.get(id).sock);
                        } catch (NumberFormatException e) {
                            System.out.println("Invalid syntax");
                        }
                    else System.out.println("Unknown command");
                }
            }
        }
    }

    private class Worker extends Thread {
        private long id;
        private ObjectInputStream is;
        private ObjectOutputStream os;
        private String name;
        private Socket sock;

        public Worker(Socket sock) throws IOException {
            this.sock = sock;
            is = new ObjectInputStream(sock.getInputStream());
            os = new ObjectOutputStream(sock.getOutputStream());
            this.id = counter.getAndIncrement();
            name = String.format("Client[%d]@%s:%s", id, sock.getInetAddress().toString(), sock.getPort());
        }

        public synchronized void send(Long id, Message msg) {
            try {
                if (id != this.id) {
                    os.writeObject(msg);
                }
            } catch (Exception e) {
                logexception(e);
            }
        }

        public void run() {
            try {
                workersMap.put(id, this);
                Thread.currentThread().setName(name);
                log.info("Connected");
                while (true) {
                    try {
                        final Message msg = (Message) is.readObject();
                        log.info("msg: " + msg.getData());
                        workersMap.forEach((id, worker) -> worker.send(this.id, msg));
                    } catch (ClassNotFoundException e) {
                        log.error("Decoding failed");
                        logexception(e);
                    }
                }
            } catch (IOException e) {
                logexception(e);
            } finally {
                IOUtils.closeQuietly(sock);
                log.info("Disconnected");
                workersMap.remove(id);
            }
        }
    }

    public static void main(String[] args) {
        final Server server = new Server(8100);
        server.serve();
    }
}