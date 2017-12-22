package ru.track.prefork;

import org.jetbrains.annotations.NotNull;
import org.slf4j.LoggerFactory;
//import java.examples.SimpleExample;
import java.sql.Connection;
import java.util.*;
import java.net.InetAddress;
import java.net.ProtocolException;
import java.net.ServerSocket;
import java.net.Socket;
import java.io.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.Scanner;

import org.slf4j.Logger;
import org.apache.commons.io.IOUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 */
public class Server {
    private int port;
    static Logger log = LoggerFactory.getLogger(Server.class);
    private AtomicLong counter = new AtomicLong(0);
    private Protocol<Message> protocol;
    private Map<Long, Worker> workerMap;


    public Server(int port, Protocol<Message> protocol) {

        this.port = port;
        this.protocol = protocol;
        workerMap = new ConcurrentHashMap<>();

    }

    public void serve() throws Exception {

        ServerSocket serverSocket = null;

        try {
            serverSocket = new ServerSocket(port, 10, InetAddress.getByName("localhost"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        Scanner scanner = new Scanner(System.in);

        Thread scannerThread = new Thread(() -> {
            try {
                while (true) {
                    String line = scanner.nextLine();
                    Pattern p = Pattern.compile("^drop (\\d+)$");
                    Matcher m = p.matcher(line);

                    if (line.equals("list")) {
                        log.info("list");
                        for (Map.Entry<Long, Worker> entry : workerMap.entrySet()) {
                            Worker value = entry.getValue();
                            System.out.println(value);
                        }

                    } else if (m.matches()) {
                        Long id = Long.parseLong(line.substring(5));

                        if (workerMap.containsKey(id)) {
                            workerMap.get(id).interrupt();
                            IOUtils.closeQuietly(workerMap.get(id).socket);
                            workerMap.remove(id);
                            log.info("drop " + id.toString() + " client");
                        } else {
                            log.info("No such client!");
                        }

                    } else {
                        log.info("Invalid command");
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        });

        scannerThread.start();

        while (true) {
            log.info("on select...");
            final Socket socket = serverSocket.accept();
            final long workerId = counter.getAndIncrement();
            Worker worker = new Worker(socket, protocol, workerId);
            workerMap.put(workerId, worker);
            worker.start();

        }

    }


    class Worker extends Thread {
        private long id;
        @NotNull
        private Socket socket;
        @NotNull
        private Protocol<Message> protocol;
        @NotNull
        private OutputStream out;

        public Worker(@NotNull Socket socket, @NotNull Protocol<Message> protocol, long id) throws Exception {
            this.socket = socket;
            this.id = id;
            this.protocol = protocol;
            setName(String.format("Client[%d]@%s:%d", id, socket.getInetAddress(), socket.getPort()));
            out = socket.getOutputStream();
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
                e.printStackTrace();

            }
        }

        private void handleSocket(Socket socket) throws IOException, ProtocolException {
            final InputStream in = socket.getInputStream();
            byte[] buf = new byte[1024];
            while (true) {
                int nRead = in.read(buf);
                if (nRead != -1) {
                    Message fromClient = protocol.decode(buf);
                    fromClient.text = ">" + fromClient.text;

                    workerMap.forEach((aLong, worker) -> {
                        if (worker.id != id)
                            worker.send(fromClient);

                    });
                } else {
                    log.error("Connection failed");
                    return;
                }
            }
        }


    }

    public static void main(String[] args) throws Exception {
        Server server = new Server(9000, new BinaryProtocol<>());
        server.serve();
    }
}


