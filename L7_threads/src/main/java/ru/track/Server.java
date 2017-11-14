package ru.track;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sun.misc.InnocuousThread;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

public class Server {
    private int port;
    private ServerSocket serverSocket;
    private static Logger logger = LoggerFactory.getLogger("Server");
    private static Map<Integer, ServerReaderThread> idReaderMap = Collections.synchronizedMap(new HashMap<>());
    private static Map<Integer, ServerWriterThread> idWriterMap = Collections.synchronizedMap(new HashMap<>());
    private static int currentId = 0;

    public Server(int port) {
        this.port = port;
    }

    private void start() throws IOException {
        try {
            serverSocket = new ServerSocket(port);
        } catch (IOException e) {
            logger.error("Couldn't start on port {}", port);
            throw e;
        }
    }

    private void shutdown() throws IOException {
        if (serverSocket != null)
            serverSocket.close();
    }

    static class ServerReaderThread extends Thread {
        private Socket socket;
        private int ID = currentId;

        private String threadName;
        private String clientName;

        public ServerReaderThread(Socket clientSocket, int clientId) {
            socket = clientSocket;
            this.ID = clientId;
            threadName = "Client["
                    + Integer.toString(ID)
                    + "]@"
                    + socket.getInetAddress().toString()
                    + ":"
                    + socket.getPort();

            clientName = "Client@"
                    + socket.getInetAddress().toString()
                    + ":"
                    + socket.getPort();
        }

        @Override
        public void run() {
            logger.info("New client");
            Thread.currentThread().setName(threadName);

            try (ObjectInputStream ios = new ObjectInputStream(socket.getInputStream())) {
                Message msg;
                while (!isInterrupted()) {
                    msg = (Message) ios.readObject();
                    if (isInterrupted())
                        return;

                    logger.info(" Got request from client {}: {}", threadName, msg.data);
                    if (msg.data.equals("exit") || currentThread().isInterrupted()) {
                        logger.info("{} left", clientName);
                        return;
                    } else {
                        synchronized (idReaderMap) {
                            for (Map.Entry<Integer, ServerWriterThread> e : idWriterMap.entrySet()) {
                                if (e.getValue().ID != ID) {
                                    synchronized (e.getValue().toSend) {
                                        e.getValue().toSend.add(new Message(msg.ts,
                                                clientName
                                                        + ">"
                                                        + msg.data));
                                    }
                                }
                            }
                        }
                    }
                }

            } catch (IOException e) {
                if (!isInterrupted())
                    logger.error("Some troubles: {}", e);
            } catch (ClassNotFoundException e) {
                logger.error("Wrong message from client");
            } finally {
                try {
                    if (!socket.isClosed())
                        socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                idReaderMap.remove(ID);
            }
        }
    }

    static class ServerWriterThread extends Thread {
        BlockingQueue<Message> toSend = new LinkedBlockingQueue<>();

        private Socket socket;
        private int ID;

        public ServerWriterThread(Socket clientSocket, int clientID) {
            socket = clientSocket;
            ID = clientID;
        }

        @Override
        public void run() {
            try (ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream())) {
                while (!currentThread().isInterrupted()) {
                    synchronized (toSend) {
                        while (!toSend.isEmpty()) {
                            Message msg = toSend.poll(100, TimeUnit.MILLISECONDS);
                            oos.writeObject(msg);
                            oos.flush();
                            if (!msg.connected) {
                                return;
                            }
                        }
                    }
                }

            } catch (IOException e) {
                if (!isInterrupted()) {
                    e.printStackTrace();
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                try {
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                idWriterMap.remove(ID);
            }
        }
    }

    public static void main(String[] args) throws IOException {
        Server server = new Server(Integer.parseInt(args[0]));
        server.start();
        Thread adminTerminal = new Thread() {
            @Override
            public void run() {
                Scanner sc = new Scanner(System.in);
                String input;
                int ID;
                while (true) {
                    input = sc.nextLine();
                    if (input.equals("list")) {
                        synchronized (idReaderMap) {
                            idReaderMap.forEach((key, value) -> {
                                System.out.println(value.threadName);
                            });

                            System.out.println();
                        }
                    } else if (input.matches("drop \\d+")) {
                        ID = Integer.parseInt(input.split(" ")[1]);
                        idReaderMap.get(ID).interrupt();
                        idWriterMap.get(ID).toSend.add(new Message(0L, null, false));
                        logger.info("Dropped " + ID);
                    }


                }
            }
        };




        adminTerminal.start();

        while(true)

    {
        Socket clientSocket = server.serverSocket.accept();
        currentId++;
        ServerReaderThread newReaderThread = new ServerReaderThread(clientSocket, currentId);
        ServerWriterThread newWriterThread = new ServerWriterThread(clientSocket, currentId);
        idReaderMap.put(currentId, newReaderThread);
        idWriterMap.put(currentId, newWriterThread);
        newReaderThread.start();
        newWriterThread.start();
    }

}

}
