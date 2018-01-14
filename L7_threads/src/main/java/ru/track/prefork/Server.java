package ru.track.prefork;

import static java.lang.Math.toIntExact;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Arrays;
import java.util.Scanner;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import org.apache.commons.io.IOUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.track.prefork.protocol.JsonProtocol;
import ru.track.prefork.protocol.Message;
import ru.track.prefork.protocol.Protocol;
import ru.track.prefork.protocol.ProtocolException;


public class Server {

    private static Logger log = LoggerFactory.getLogger(Server.class);
    @Nullable
    private final ExecutorService pool;
    private int port;
    private AtomicLong serverCounter = new AtomicLong(0);
    private Protocol<Message> protocol;
    private Future[] fut;
    private int maxPoolSize;
    private int maxClientsNum;


    private ConcurrentMap<Long, Worker> workerMap;

    private Server(int port, Protocol<Message> protocol, ExecutorService pool, int maxPoolSize) {
        this.port = port;
        this.protocol = protocol;
        this.pool = pool;
        this.maxClientsNum = 100;
        workerMap = new ConcurrentHashMap<>();
        this.maxPoolSize = maxPoolSize;
        fut = new Future[this.maxClientsNum];
    }

    public static void main(String[] args) {
        int maxPoolSize = 3;
        final BlockingQueue<Runnable> queue = new LinkedBlockingQueue<Runnable>();

        final ThreadPoolExecutor pool = new ThreadPoolExecutor(3, maxPoolSize, 0L,
            TimeUnit.MILLISECONDS,
            queue);

        Server server = new Server(9000, new JsonProtocol(), pool, maxPoolSize);
        try {
            server.serve();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void serve() throws Exception {
        log.info("Server started!");
        ServerSocket serverSocket = new ServerSocket(port, 10, InetAddress.getByName("localhost"));

        Scanner scanner = new Scanner(System.in);
        Thread adminThread = new Thread(() -> {
            while (true) {
                String line = scanner.nextLine();
                if (line.equals("list")) {
                    workerMap.forEach((longId, worker) -> {
                        log.info(worker.getName());
                    });
                } else if (line.startsWith("drop")) {
                    String[] command = line.split(" ");
                    if (command.length == 2) {
                        try {
                            Worker worker = workerMap.get(Long.parseLong(command[1]));
                            fut[toIntExact(worker.id)].cancel(true);
                            log.info(String.format("Interrupting: %s", worker.getName()));
                        } catch (NumberFormatException e) {
                            log.error(line + " <- Wrong syntax of drop.", e);
                        }
                    } else {
                        log.error(line + " <- Wrong syntax of drop.");
                    }
                } else {
                    log.error(line + " <- Wrong command.");
                }
            }
        });
        adminThread.setName("AdminThread");
        adminThread.start();
        while (!serverSocket.isClosed() && serverCounter.get() < maxClientsNum) {
            final Socket socket = serverSocket.accept();
            log.info("accepted");
            TimeUnit.MILLISECONDS.sleep(500);
            final long workerId = serverCounter.getAndIncrement();
            Worker worker = new Worker(socket, protocol, workerId);
            workerMap.put(workerId, worker);
            fut[toIntExact(workerId)] = pool.submit(worker);
        }
    }

    class Worker extends Thread {

        @NotNull
        final OutputStream out;
        @NotNull
        final InputStream in;
        @NotNull
        Socket socket;
        @NotNull
        Protocol<Message> protocol;
        private long id;

        Worker(@NotNull Socket socket, @NotNull Protocol<Message> protocol, long id)
            throws IOException {
            this.socket = socket;
            this.protocol = protocol;
            this.id = id;
            setName(
                String.format("Client[%d]@%s:%d", id, socket.getInetAddress(), socket.getPort()));

            out = socket.getOutputStream();
            in = socket.getInputStream();
        }

        @Override
        public void run() {
            try {
                log.info("Connected");
                handleSocket(socket);
            } catch (Exception e) {
                workerMap.remove(id);
            }
        }

        private void send(Message message) {
            try {
                out.write(protocol.encode(message));
                out.flush();
            } catch (IOException e) {
                log.error("IO exception", e);
            } catch (ProtocolException e) {
                log.error("Protocol exception", e);
            }
        }

        private void handleSocket(Socket socket) throws IOException {
            try {
                while (!socket.isClosed() && !Thread.currentThread().isInterrupted()) {
                        byte[] buffer = new byte[1024];
                        int nbytes = in.read(buffer);
                        if (nbytes != -1) {
                            Message msgFromClient = protocol
                                .decode(Arrays.copyOfRange(buffer, 0, nbytes),
                                    Message.class);
                            log.info("decode: " + msgFromClient);

                            if (msgFromClient.text.equalsIgnoreCase("exit")) {
                                log.info("Client sent exit");
                                Thread.currentThread().interrupt();
                            }
                            msgFromClient.text = String
                                .format("Client@%s:%d>%s", socket.getInetAddress(),
                                    socket.getPort(), msgFromClient.text);

                            workerMap.forEach((longId, worker) -> {
                                if (longId != id) {
                                    worker.send(msgFromClient);
                                }
                            });

                        } else {
                            Thread.currentThread().interrupt();
                        }
                }
            } catch (IOException e) {
                log.error("IOException", e);
            } catch (ProtocolException e) {
                e.printStackTrace();
            } finally {
                IOUtils.closeQuietly(socket);
                workerMap.remove(id);
                log.info("Dropped");
            }
        }
    }
}
