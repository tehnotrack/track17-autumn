package ru.track.prefork;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Map;
import java.util.Queue;
import java.util.Scanner;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicLong;

public class Server {
    private boolean killServer = false;
    private int nWorkers;
    private AtomicLong nextId = new AtomicLong(0); //next free worker id
    private int serverPort;
    private static Logger log = LoggerFactory.getLogger(Server.class);
    private static Protocol<Message> protocol = new BinaryProtocol<>();
    private Map<Long, Worker> workerMap = new ConcurrentHashMap<>();
    private Queue<Message> messageQueue = new ConcurrentLinkedQueue<>();

    private Server(int serverPort, int workerNmb) {
        this.nWorkers = workerNmb;
        this.serverPort = serverPort;
    }

    public static void main(String[] args) {
        Server server = new Server(9000, 5);
        server.serve();
    }

    private void serve() {
        ServerSocket serverSocket = null;
        try {
            serverSocket = new ServerSocket(serverPort, 10, InetAddress.getByName("localhost"));
        } catch (IOException e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        }

        new Thread(this::sendServe).start();

        new Thread(this::consoleServe).start();

        ExecutorService pool = Executors.newFixedThreadPool(nWorkers);

        log.info("server started");

        while (true) {
            final Socket socket;
            try {
                socket = serverSocket.accept();
                pool.submit(() -> handleSocket(socket, nextId.getAndIncrement()));
            } catch (IOException e) {
                System.out.println(e.getMessage());
                e.printStackTrace();
                break;
            }
        }
        killServer = true;
        while (!workerMap.isEmpty())
            for (Worker w : workerMap.values())
                w.deadSession = true;
        try {
            serverSocket.close();
        } catch (IOException e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        }

    }

    private void consoleServe() {
        Thread.currentThread().setName("admin console");
        try (Scanner scanner = new Scanner(System.in)
        ) {
            while (true) {
                if (killServer)
                    break;
                String string = scanner.nextLine();

                if (string.equals("list"))
                    list();

                if (string.matches("drop \\d+")) {
                    long id = Long.parseLong(string.split(" ")[1]);
                    dropWorker(id);
                }
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        }

    }

    private void list() {
        if (workerMap.isEmpty()) {
            System.out.println("Nobody is here");
            return;
        }
        for (Worker w : workerMap.values()) {
            System.out.println(String.format("Client[%d]@[%s]:[%d]", w.id, w.socket.getInetAddress().toString(), w.socket.getPort()));
        }
    }

    private void dropWorker(long dropId) throws Exception {
        if (workerMap.containsKey(dropId)) {
            workerMap.get(dropId).drop();
        } else log.info("user not exits");
    }

    private void sendServe() {
        Thread.currentThread().setName("Sender");
        while (true) {
            if (!messageQueue.isEmpty()) {
                send(messageQueue.poll());
            }
        }
    }

    private void send(Message message) {
        long senderId = message.senderId();
        for (Long id : workerMap.keySet()) {
            if (id != senderId) {
                try {
                    workerMap.get(id).outputStream.write(protocol.encode(message));
                } catch (IOException e) {
                    System.out.println(e.getMessage());
                    e.printStackTrace();
                }
            }
        }
    }

    private void handleSocket(Socket socket, long id) {
        Thread.currentThread().setName(String.format("Client[%d]@%s:%s", id, socket.getInetAddress(), socket.getPort()));
        log.info("connected");

        Worker worker = new Worker(id, socket);
        workerMap.put(id, worker);
        while (true) {
            Message message = worker.listen();
            if (worker.deadSession) {
                worker.endSession();
                workerMap.remove(worker.id);
                break;
            }
            if (message != null)
                System.out.printf("Client[%d]@%s:%s > %s\n", id, socket.getInetAddress(), socket.getPort(), message.toString());
            messageQueue.add(message);
        }
    }
}