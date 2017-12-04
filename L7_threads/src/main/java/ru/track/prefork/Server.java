package ru.track.prefork;

import org.apache.commons.io.IOUtils;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicLong;

/**
 *
 */
public class Server {
    public static Logger log = LoggerFactory.getLogger(Server.class);

    private int port;
    private Protocol<Message> protocol;
    private AtomicLong idCounter = new AtomicLong();
    private ConcurrentMap<Long, Worker> activeClients = new ConcurrentHashMap<>();

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
        ExecutorService pool = new ThreadPoolExecutor(20, 100,
                60L, TimeUnit.SECONDS,
                new LinkedBlockingQueue<>(),
                (r) -> {
                    Thread thread = new Thread(r);
                    thread.setDaemon(true);
                    return thread;
                }
        );

        while (true) {
            try {
                Socket socket = serverSocket.accept();
                log.info("Client accepted");
                Worker client = new Worker(socket);
                activeClients.put(client.getId(), client);
                pool.submit(client);
            } catch(IOException e){
                log.info("connection failed.");
            }
        }
//        pool.shutdown();
//        log.info("Server stoped!");
    }

    private void broadcast(Message msg, long id) throws IOException,
            ProtocolException,
            ServerByteProtocolException {
        for (Worker w: activeClients.values()) {
            if (w.getId() == id) continue;
            ServerByteProtocol sbp = new ServerByteProtocol(w.getSocket());
            sbp.write(protocol.encode(msg));
        }
    }

    private void handleSocket(Socket socket, long id) throws IOException, ProtocolException, ServerByteProtocolException {
        ServerByteProtocol serverByteProtocol = new ServerByteProtocol(socket);
        serverByteProtocol.write(protocol.encode(new Message("Enter your name")));
        log.info("Setting name...");
        activeClients.get(id).setName(protocol.decode(serverByteProtocol.read()).getText());
        Message name = new Message(System.currentTimeMillis(), "Client " + activeClients.get(id).getName() + " have joined the server.");
        broadcast(name, id);
        while(true) {
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
                broadcast(new Message("Client " + activeClients.get(id).getName() + " left the server."), id);
            } catch (IOException e) {
                log.error(e.getClass().getName() + ": " + e.getMessage());
            } catch (ProtocolException e) {
                log.error(e.getClass().getName() + ": " + e.getMessage());
            } catch (ServerByteProtocolException e) {
                log.error(e.getClass().getName() + ": " + e.getMessage());
            } finally {
                activeClients.remove(id);
                IOUtils.closeQuietly(socket);
                log.info("Connection closed.");
            }
        }
    }

    class Handler extends Thread {

    }
}
