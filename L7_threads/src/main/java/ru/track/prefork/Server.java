package ru.track.prefork;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;


public class Server {
    private int nextId; //next free worker id
    private int port;
    private Map<Integer, Worker> idMap; //map of active ids and their workers
    private static Logger log = LoggerFactory.getLogger(Server.class);


    public Server(int port) {
        this.port = port;
        this.nextId = 0;
        this.idMap = new LinkedHashMap<>();
    }


    private class Worker extends Thread {
        private boolean send_succeeded;
        private ReadWorker readWorker;
        private WriteWorker writeWorker;
        private Socket socket;
        private int id;


        private class ReadWorker extends Thread {
            private boolean deadSession; //session is dead if true
            private InputStream inputStream;

            private ReadWorker() {
                inputStream = null;
                deadSession = false;
            }

            private boolean stopMe() {
                if (deadSession)
                    return true;
                return false;
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
                            System.out.println(this.getName() + "> " + new String(buffer, 0, nRead));
                            broadcast(buffer, nRead, id);
                        } else {
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
            private boolean stop; //asks to stop the session if true
            private int nRead;
            private byte[] msgToWrite;
            private OutputStream outputStream;

            private void putMsg(byte[] msg, int nRead) throws IOException {
                log.info("put msg");
                msgToWrite = new byte[nRead];
                msgToWrite = Arrays.copyOf(msg, nRead);
                this.nRead = nRead;
                log.info("Sending " + new String(msgToWrite, 0, nRead));
                outputStream.write(msgToWrite, 0, nRead);
                outputStream.flush();
                msgToWrite = null;
                nRead = 0;
                send_succeeded = true;
                while (true) {
                    if (send_succeeded) {
                        send_succeeded = false;
                        break;
                    }
                }
            }

            private boolean hasMsgToWrite() {
                if (msgToWrite != null && msgToWrite.length > 0 && nRead > 0)
                    return true;
                return false;
            }


            private WriteWorker() {
                msgToWrite = new byte[2048];
                outputStream = null;
                stop = false;
            }

            @Override
            public void run() {
                writeWork();
            }

            private void writeWork() {
                try {
                    outputStream = socket.getOutputStream();
                    while (true) {
//                        if (hasMsgToWrite()) {
//                            log.info("Sending " + new String(msgToWrite, 0, nRead));
//                            outputStream.write(msgToWrite, 0, nRead);
//                            outputStream.flush();
//                            msgToWrite = null;
//                            nRead = 0;
//                            send_succeeded = true;
//                        }
                        if (stop)
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


        private Worker(Socket socket, int id) {
            this.send_succeeded = false;
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
                writeWorker.start();
                while (true) {
                    if (readWorker.stopMe()) {
                        writeWorker.stop = true;
                        break;
                    }
                    else continue;
                }
            } catch (Exception e) {
                System.out.println(e.getMessage());
            } finally {
                idMap.remove(id);
            }
            return;

        }

    }

    private void broadcast(@NotNull byte[] msg, @NotNull int nRead, @Nullable int passId) {
        log.info("Start broadcasting " + new String(msg, 0, nRead));
        for (Integer id : idMap.keySet()) {
            if (id.equals(passId))
                continue;

            try {
                idMap.get(id).writeWorker.putMsg(msg, nRead);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    private void serve() {
        ServerSocket serverSocket = null;
        try {
            serverSocket = new ServerSocket(port, 10, InetAddress.getByName("localhost"));
        } catch (Exception e) {
            System.out.println("Couldn't run server:\n" + e.getMessage());
            return;
        }
        System.out.println("Server runs!\n");

        try {
            while (true) {
                Socket socket = serverSocket.accept();
                Worker w = new Worker(socket, nextId);
                idMap.put(nextId, w);
                w.setName(String.format("Client%d@%s:%s", nextId++, socket.getInetAddress(), socket.getPort()));
                w.start();
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        } finally {

            try {
                serverSocket.close();
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
