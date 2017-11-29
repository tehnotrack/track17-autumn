package ru.track.prefork;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.InetAddress;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicLong;

import org.jetbrains.annotations.NotNull;

import ru.track.prefork.protocol.Protocol;
import ru.track.prefork.protocol.Message;
import ru.track.prefork.protocol.ProtocolException;
import ru.track.prefork.protocol.JavaSerializationProtocol;

import static java.lang.Thread.sleep;

/**
 *
 */


public class Server {
    static Logger log = LoggerFactory.getLogger(Server.class);
    private int port;
    private AtomicLong serverCounter = new AtomicLong(0);
    private Protocol<Message> protocol;

    private ConcurrentMap<Long, Worker> workerMap;


    public Server(int port, Protocol<Message> protocol) {
        this.port = port;
        this.protocol = protocol;
        workerMap = new ConcurrentHashMap<>();
    }


    public void serve() throws IOException {

        ServerSocket serverSocket = null;
        try {
            serverSocket = new ServerSocket(port, 10, InetAddress.getByName("localhost"));
        } catch (IOException e) {
            e.printStackTrace();
        }


        //begin of the console

        Scanner scan = new Scanner(System.in);

        Runnable consoleScanner = () -> {

            while (true) {

                String str = scan.nextLine().toLowerCase();

                if (str.equals("list")) {
                    if (!workerMap.isEmpty()) {
                        System.out.println("List of connections:");
                        for (Worker i : workerMap.values()) {
                            System.out.println(i.getName());
                        }
                    } else System.out.println("List of connections is empty");
                } else if (str.startsWith("drop ")) {
                    Long id = Long.parseLong(str.substring(5));
                    if (workerMap.get(id) != null) {
                        Message dropNotificationMsg = new Message(0, "Your connection was deactivated");
                        workerMap.get(id).send(dropNotificationMsg);
                        try {
                            workerMap.get(id).socket.close();
                        } catch (IOException e) {
                            System.err.println("Socket was not closed correctly");
                            e.printStackTrace();
                        }
                        workerMap.remove(id);
                        System.out.println("Dropped successfully");


                    } else {
                        System.err.println("Drop: error\nClient does not exist");
                    }

                } else {
                    System.out.println("Unknown command");
                }

            }
        };
        new Thread(consoleScanner).start();

        //end of the console


        while (true) {
            try {
                log.info("serving...");

                final Socket socket = serverSocket.accept(); //Listens for a connection to be made to this socket and accepts it.
                final long workerId = serverCounter.getAndIncrement();
                Worker worker = new Worker(socket, protocol, workerId);
                workerMap.put(workerId, worker);
                worker.start();

            } catch (IOException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    class Worker extends Thread {
        private long id;

        @NotNull
        private Socket socket;

        @NotNull
        private Protocol<Message> protocol;

        @NotNull
        OutputStream out;


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
                System.err.println("Socket closed");
                //throw new RuntimeException(e);
            }
        }

        private void send(Message message) {
            try {
                out.write(protocol.encode(message));
                out.flush();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ProtocolException e) {
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
                    fromClient.data = ">" + fromClient.data;
                    workerMap.forEach((aLong, worker) -> worker.send(fromClient));
                } else {
                    log.error("Connection failed");
                    return;
                }
            }
        }
    }

    public static void main(String[] args) throws IOException {
        Server myserv = new Server(9000, new JavaSerializationProtocol());
        myserv.serve();
    }


}