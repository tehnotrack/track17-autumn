package ru.track.prefork;

import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;


public class Server {
    private AtomicLong nextId; //next free worker id
    private int port;
    private Map<Long, Worker> idMap; //map of active ids and their workers
    private static Logger log = LoggerFactory.getLogger(Server.class);
    private static Protocol<Message> protocol = new BinaryProtocol<>();


    public Server(int port) {
        this.port = port;
        this.nextId = new AtomicLong(0);
        this.idMap = new LinkedHashMap<>();
    }


    private class Worker extends Thread {
        private boolean deadSession; //session is dead if true
        private ReadWorker readWorker;
        private WriteWorker writeWorker;
        private Socket socket;
        private long id;

        public void kill() {
            deadSession = true;
            log.info("kicking");
        }


        private class ReadWorker extends Thread {
            private InputStream inputStream;

            private ReadWorker() {
                inputStream = null;
                deadSession = false;
            }


            @Override
            public void run() {
                readWork();
            }

            private void readWork() {
                byte[] buffer = new byte[2048];
                try {
                    inputStream = socket.getInputStream();
                    while (true) {
                        int nRead = inputStream.read(buffer);
                        if (nRead > 0) {
                            Message msg = new Message(buffer, id, nRead);
                            System.out.println(this.getName() + "> " + msg.toString());
                            broadcast(msg);
                        }
                        if (deadSession) {
                            deadSession = true;
                            log.info("force disconnect");
                            break;
                        }
                        if (nRead <= 0) {
                            deadSession = true;
                            log.info("Disconnected");
                            break;
                        }
                    }
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                    e.printStackTrace();
                } finally {
                    try {
                        inputStream.close();
                    } catch (IOException e) {
                        System.out.println(e.getMessage());
                        e.printStackTrace();
                    }
                }
            }
        }

        private class WriteWorker extends Thread {
            private AtomicInteger nRead;
            private Message msgToWrite;
            private OutputStream outputStream;
            private AtomicBoolean can_work;

            private void putMsg(Message msg) throws IOException { //method is called from outside
                log.info("put msg");
//                System.arraycopy(msg, 0, msgToWrite, 0, nRead);
                msgToWrite = new Message(msg);
//                this.nRead.set(nRead);
                this.can_work.set(true);
            }

            private WriteWorker() {
//                msgToWrite = new byte[2048];
                nRead = new AtomicInteger(0);
                can_work = new AtomicBoolean(false);
                outputStream = null;
            }

            @Override
            public void run() {
                writeWork();
            }

            private void writeWork() {
                try {
                    outputStream = socket.getOutputStream();
                    while (true) {
                        if (can_work.get()) { //send messages
                            log.info("Sending " + msgToWrite.toString());
//                            outputStream.write(msgToWrite, 0, nRead.get());
                            outputStream.write(protocol.encode(msgToWrite));
                            outputStream.flush();
                            can_work.set(false);
                        }
                        if (deadSession)
                            break;
                    }
                } catch (IOException e) {
                    System.out.println(e.getMessage());
                    e.printStackTrace();
                }
                finally {
                    try {
                        outputStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }


        private Worker(Socket socket, long id) {
            this.socket = socket;
            this.id = id;
        }

        @Override
        public void run() {
            work();
        }

        private void work() {

            try {
                log.info("Connected");
                readWorker = new ReadWorker();
                writeWorker = new WriteWorker();
                writeWorker.setName(this.getName() + "_Writer");
                readWorker.setName(this.getName() + "_Reader");
                readWorker.start();
                writeWorker.run(); // staying in this thread
                while (true) {
                    if (deadSession) {
                        break;
                    }
                }
            } catch (Exception e) {
                System.out.println(e.getMessage());
            } finally {
                idMap.remove(id);
            }
        }

    }

    private void broadcast(@NotNull Message msg) {
        log.info("Start broadcasting " + msg.toString());
        for (Long id : idMap.keySet()) {
            if (id.equals(msg.senderId()))
                continue;

            try {
                idMap.get(id).writeWorker.putMsg(msg);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    class Console extends Thread {
        private boolean kill = false;
        private void kill() { //interrupt
            kill = true;
        }
        @Override
        public void run() { consoleWork(); }

        private void list() {
            if (idMap.isEmpty()) {
                System.out.println("Nobody is here");
                break;
            }
            for (Map.Entry<Long, Worker> entry: idMap.entrySet()) {
                System.out.println(String.format("Client[%d]@[%s]:[%d]", entry.getKey(), entry.getValue().socket.getLocalAddress().toString(), entry.getValue().socket.getPort()));
            }
        }

        private void dropWorker(long dropId) throws Exception{
            if (idMap.containsKey(dropId)) {
                idMap.get(dropId).kill();
            }
            else log.info("user not exits");
        }

        private void consoleWork() {

            try ( Scanner scanner = new Scanner(System.in);
            ) {
                while (true) {
                    if (kill == true)
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

    }



    private void serve() {
        ServerSocket serverSocket;
        Console console = new Console();
        try {
            serverSocket = new ServerSocket(port, 10, InetAddress.getByName("localhost"));
        } catch (Exception e) {
            System.out.println("Couldn't run server:\n" + e.getMessage());
            return;
        }
        System.out.println("Server runs!\n");

        try {
            console.setName("AdminConsole");
            console.start();
            while (true) {
                Socket socket = serverSocket.accept();
                Worker w = new Worker(socket, nextId.get());
                idMap.put(nextId.get(), w);
                w.setName(String.format("Client%d@%s:%s", nextId.getAndIncrement(), socket.getInetAddress(), socket.getPort()));
                w.start();
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        } finally {

            try {
                serverSocket.close();
                console.kill();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    public static void main(String[] args) throws Exception {
        Server server = new Server(9000);
        server.serve();
    }
}