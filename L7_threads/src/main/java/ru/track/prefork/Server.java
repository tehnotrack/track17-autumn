package ru.track.prefork;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;



public class Server {
    private int nextWorkerID;
    private int port;

    private Map<Integer, Worker> mappedWorkerID;
    private static Logger log = LoggerFactory.getLogger(Server.class);


    public Server(int port) {
        this.port = port;
        this.nextWorkerID = 0;
        this.mappedWorkerID = new LinkedHashMap<>();
    }


    private class Worker extends Thread {
        private boolean sessionIsInterrupted;
        private myServerReader mySingleWorker;
        private myWriter singleWriter;
        private Socket socket;
        private int id;


        private class myServerReader  extends Thread {
            private InputStream myStream;

            private myServerReader () {
                myStream = null;
                sessionIsInterrupted = false;
            }


            @Override
            public void run() {
                readerFunc ();
            }

            private void readerFunc () {
                byte[] buffer = new byte[1024];
                try {
                    myStream = socket.getInputStream();
                    while (true) {
                        int amountOfBytes = myStream.read(buffer);
                        if (amountOfBytes > 0) {
                            System.out.println(this.getName() + "> " + new String(buffer, 0, amountOfBytes));
                            broadcastMailing(buffer, amountOfBytes, id);
                        } else {
                            sessionIsInterrupted = true;
                            log.info("Lost connection to server! Please contact your administrator.");
                            break;
                        }
                    }
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                    e.printStackTrace();
                } finally {
                    try {
                        myStream.close();
                    } catch (IOException e) {
                        System.out.println(e.getMessage());
                        e.printStackTrace();
                    }
                }
            }
        }

        private void myWorkProcess () {

            try {
                log.info("Client has connected");
                mySingleWorker = new myServerReader ();
                singleWriter = new myWriter();
                singleWriter.setName(this.getName() + "_Writer");
                mySingleWorker .setName(this.getName() + "_Client");
                mySingleWorker .start();
                singleWriter.run();
                while (true) {
                    if (sessionIsInterrupted) {
                        break;
                    }
                    else continue;
                }
            } catch (Exception e) {
                System.out.println(e.getMessage());
            } finally {
                mappedWorkerID.remove(id);
            }
            return;

        }

        private class myWriter extends Thread {

            private OutputStream outputStream;

            private void sendMessagesg(byte[] msg, int amountOfBytes) throws IOException {

                outputStream.write(msg, 0, amountOfBytes);
                outputStream.flush();
            }

            private myWriter() {

                outputStream = null;
            }

            @Override
            public void run() {
                writeProcess();
            }

            private void writeProcess() {
                try {
                    outputStream = socket.getOutputStream();
                    while (true) {

                        if (sessionIsInterrupted)
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
            this.socket = socket;
            this.id = id;
        }

        @Override
        public void run() {
            myWorkProcess ();
        }



    }

    private void broadcastMailing(@NotNull byte[] message, @NotNull int amountOfBytes, @Nullable int sendersID) {
        log.info("Start broadcasting " + new String(message, 0, amountOfBytes));
        for (Integer id : mappedWorkerID.keySet()) {
            if (id.equals(sendersID))
                continue;

            try {
                mappedWorkerID.get(id).singleWriter.sendMessagesg(message, amountOfBytes);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    private void workInCycle() {
        ServerSocket serverSocket = null;
        try {
            serverSocket = new ServerSocket(port, 10, InetAddress.getByName("localhost"));
        } catch (Exception e) {
            System.out.println("Server hasn't started. Following error occured:\n" + e.getMessage());
            return;
        }
        System.out.println("Server is runnig!\n");

        try {
            while (true) {
                Socket socket = serverSocket.accept();
                Worker myAnotherWorker = new Worker(socket, nextWorkerID);
                mappedWorkerID.put(nextWorkerID, myAnotherWorker);
                myAnotherWorker.setName(String.format("Client%d@%s:%s", nextWorkerID++, socket.getInetAddress(), socket.getPort()));
                myAnotherWorker.start();
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
        Server server = new Server(8000);
        server.workInCycle();
    }
}