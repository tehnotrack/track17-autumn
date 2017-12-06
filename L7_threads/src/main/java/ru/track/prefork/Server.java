package ru.track.prefork;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 */
public class Server {
    public static Logger log = LoggerFactory.getLogger(Server.class);
    private ServerByteProtocol instance;
    private int port;
    private Protocol<Message> protocol;
    private AtomicLong idCounter = new AtomicLong();
    private ConcurrentMap<Long, Worker> activeClients = new ConcurrentHashMap<>();
    private ConcurrentMap<Long, Future<?>> activeClientsTasks = new ConcurrentHashMap<>();
    private ExecutorService pool = new ThreadPoolExecutor(20, 100,
            60L, TimeUnit.SECONDS,
            new LinkedBlockingQueue<>(),
            (r) -> {
                Thread thread = new Thread(r);
                thread.setDaemon(true);
                return thread;
            }
    );

    public Server(int port, Protocol<Message> protocol) {
        this.port = port;
        this.protocol = protocol;
    }

    public void serve() {
        ServerSocket serverSocket = null;
        try {
            serverSocket = new ServerSocket(port, 10, InetAddress.getByName("localhost"));
        } catch (IOException e) {
            log.error("Host is not valid");
            return;
        }
        Handler handler = new Handler(serverSocket);
        handler.setDaemon(true);
        handler.start();
        Scanner adminIn = new Scanner(System.in);
        while(true) {
            String command = adminIn.nextLine();
            if (command.matches("^exit$")) {
                break;
            } else if (command.matches("^list$")) {
                activeClients.forEach((k, v) ->
                        System.out.println(String.format("Client[%d]@%s:%d(%s)",
                                k,
                                v.getSocket().getInetAddress(),
                                v.getSocket().getPort(),
                                v.getName()))
                );
            } else if (command.matches("^drop -id (?<id>\\d+)$")){
                Matcher m = Pattern.compile("^drop -id (?<id>\\d+)$").matcher(command);
                m.matches();
                try {
                    Long id = Long.parseLong(m.group("id"));
                    Worker worker = activeClients.get(id);
                    if (worker == null) {
                        System.out.println("Id does not exist!");
                    } else {
                        removeWorker(id);
                    }
                } catch (NumberFormatException e) {
                    System.out.println("Incorrect number!");
                }
            } else {
                System.out.println("Unknown command!");
            }
        }
    }

    private void broadcast(Message msg, long id) throws IOException,
            ProtocolException {
        for (Worker w: activeClients.values()) {
            try {
                if (w.getId() == id) continue;
                ServerByteProtocol sbp = new ServerIOByteProtocol(w.getSocket());
                sbp.write(protocol.encode(msg));
            }
            catch (ServerByteProtocolException e) {}
        }
    }

    private void broadcastQuietly(Message msg, long id) {
        try {
            broadcast(msg, id);
        } catch (Exception e) {}
    }

    private void handleSocket(Socket socket, long id) throws IOException, ProtocolException, ServerByteProtocolException {
        ServerByteProtocol serverByteProtocol = new ServerIOByteProtocol(socket);
        serverByteProtocol.write(protocol.encode(new Message("Enter your name")));
        log.info("Setting name...");
        activeClients.get(id).setName(protocol.decode(serverByteProtocol.read()).getText());
        Message name = new Message(System.currentTimeMillis(), "Client " + activeClients.get(id).getName() + " have joined the server.");
        broadcast(name, id);
        while(!Thread.currentThread().isInterrupted()) {
            log.info("Reading line...");
            byte[] buffer = serverByteProtocol.read();
            Message msg = protocol.decode(buffer);
            if (msg.getText().equals("exit")) break;
            msg = new Message(activeClients.get(id).getName(), msg.getText());
            log.info(msg.toString());
            log.info("Broadcasting...");
            broadcast(msg, id);
        }
        log.info("On exit...");
    }

    private void addWorker(Socket socket) {
        Worker client = new Worker(socket);
        activeClients.put(client.getId(), client);
        activeClientsTasks.put(client.getId(), pool.submit(client));
    }

    private void removeWorker(long id) {
        activeClientsTasks.get(id).cancel(true);
        IOUtils.closeQuietly(activeClients.get(id).getSocket());
    }

    public static void main(String... args) {
        Server server = new Server(8000, new BinaryProtocol<>());
        server.serve();
    }

    class Worker implements Runnable {

        private Socket socket;
        private long id;
        private String name = "AnonymousUser";

        public Worker(Socket socket) {
            this.socket = socket;
            id = idCounter.getAndIncrement();
        }

        public Socket getSocket() {
            return socket;
        }

        public long getId() {
            return id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        @Override
        public void run() {
            Thread.currentThread().setName(String.format("Client[%d]@%s:%d", id, socket.getInetAddress(), socket.getPort()));
            try {
                handleSocket(socket, id);

            } catch (IOException e) {
                log.error(e.getClass().getName() + ": " + e.getMessage());
            } catch (ProtocolException e) {
                log.error(e.getClass().getName() + ": " + e.getMessage());
            } catch (ServerByteProtocolException e) {
                log.error(e.getClass().getName() + ": " + e.getMessage());
            }
            broadcastQuietly(new Message("Client " + activeClients.get(id).getName() + " left the server."), id);
            activeClients.remove(id);
            activeClientsTasks.remove(id);
            IOUtils.closeQuietly(getSocket());
            log.info("Connection closed.");
        }
    }

    class Handler extends Thread {

        ServerSocket serverSocket;

        public Handler(ServerSocket serverSocket) {
            this.serverSocket = serverSocket;
        }

        public void run() {
            while (true) {
                try {
                    Socket socket = serverSocket.accept();
                    log.info("Client accepted");
                    addWorker(socket);
                } catch(IOException e){
                    log.info("connection failed.");
                }
            }
        }
    }
}
