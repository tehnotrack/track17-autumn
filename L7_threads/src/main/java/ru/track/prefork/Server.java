package ru.track.prefork;
import java.util.LinkedHashMap;
import java.util.Map;
import org.jetbrains.annotations.NotNull;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import ru.track.prefork.Protocol.Message;
import ru.track.prefork.Protocol.Protocol;
import ru.track.prefork.Protocol.MySerializationProtocol;
import ru.track.prefork.Protocol.ProtocolException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.Scanner;


public class Server {
    private AtomicLong  nextWorkerID;
    private int port;

    private static  Map<Long, Worker> mappedWorkerID;
    private static Logger log = LoggerFactory.getLogger(Server.class);
    private static Protocol<Message> protocol = new MySerializationProtocol<>();

    public Server(int port) {
        this.port = port;
        this.nextWorkerID = new AtomicLong(0);
        this.mappedWorkerID = new LinkedHashMap<>();
    }


    private class Worker extends Thread {
        private boolean sessionIsInterrupted;
        private myServerReader mySingleWorker;
        private myWriter singleWriter;
        private Socket socket;
        private long id;


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
                            Message msg = new Message(buffer, id, amountOfBytes);
                            System.out.println(this.getName() + "> " + msg.toString());
                            broadcastMailing(msg);
                        } else {
                            sessionIsInterrupted = true;
                            log.info("Lost connection to server! Please contact your administrator.");
                            break;
                        }
                    }
                } catch (Exception e) {
                    //System.out.println(e.getMessage());
                    //e.printStackTrace();
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
              //  System.out.println(e.getMessage());
            } finally {
                mappedWorkerID.remove(id);
            }
            return;

        }

        private class myWriter extends Thread {

            private OutputStream outputStream;
            private AtomicBoolean can_work;
            private AtomicInteger nRead;
            private Message msgToWrite;

          private void sendMessagesg(Message msg) throws IOException {
              log.info("put msg");
              msgToWrite = new Message(msg);
              this.can_work.set(true);
            }

            private myWriter() {
                nRead = new AtomicInteger(0);
                can_work = new AtomicBoolean(false);

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
                        if (can_work.get()) { //send messages
                            log.info("Sending " + msgToWrite.toString());
                            outputStream.write(protocol.encode(msgToWrite));
                            outputStream.flush();
                            can_work.set(false);
                        }

                        if (sessionIsInterrupted)
                            break;
                    }
                } catch (IOException e) {
                    //System.out.println(e.getMessage());
                   // e.printStackTrace();
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
            myWorkProcess ();
        }
    }


    private void broadcastMailing(@NotNull Message msg) {
        log.info("Start broadcasting " + msg.toString());

        for (Long  id : mappedWorkerID.keySet()) {
            if (id.equals(msg.senderId()))
                continue;
            try {
                mappedWorkerID.get(id).singleWriter.sendMessagesg(msg);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    private void workInCycle() {

        ServerSocket serverSocket;
        try {
            serverSocket = new ServerSocket(port, 10, InetAddress.getByName("localhost"));
        } catch (Exception e) {
            System.out.println("Server hasn't started. Following error occured:\n" + e.getMessage());
            return;
        }
        System.out.println("Server is runnig!\n");
        Scanner myScanner = new Scanner(System.in);

        Thread consoleScannerThread = new Thread() {
            @Override
            public void run() {

            while(true) {
                String str = myScanner.nextLine();
                log.info(str);
                if (str.equals("list")) {
                    System.out.println("List of connections:");
                    for (Worker u : mappedWorkerID.values()) {
                        System.out.println(u.getName());
                    }
                } else if (str.startsWith("drop ")) {
                    Long id = Long.parseLong(str.substring(5));
                    if (mappedWorkerID.get(id) != null) {
                        String dropMsg = "Your connection was terminated, sorry";
                        int amountOfBytes = dropMsg.length();
                        Message dropNotificationMsg = new Message(dropMsg.getBytes(), id, amountOfBytes);


                        try {
                            mappedWorkerID.get(id).singleWriter.sendMessagesg(dropNotificationMsg);
                        } catch(IOException e) {
                            System.err.println("Cant send message");
                            e.printStackTrace();
                        }

                        try {
                            mappedWorkerID.get(id).socket.close();
                        } catch (IOException e) {
                            System.err.println("Dropped socket was not closed properly");
                            e.printStackTrace();
                        }
                        mappedWorkerID.remove(id);
                        System.out.println("Drop: success");
                    } else {
                        System.err.println("Drop: error\nClient does not exist");
                    }

                }

            }
        }
        };


        consoleScannerThread.start();


        try {
            while (true) {
                Socket socket = serverSocket.accept();
                Worker myAnotherWorker = new Worker(socket, nextWorkerID.get());
                mappedWorkerID.put(nextWorkerID.get(), myAnotherWorker);
                myAnotherWorker.setName(String.format("Client%d@%s:%s", nextWorkerID.getAndIncrement(), socket.getInetAddress(), socket.getPort()));
                myAnotherWorker.start();
            }
        } catch (Exception e) {
           // System.out.println(e.getMessage());
        } finally {

            try {
                serverSocket.close();
            } catch (Exception e) {
               // e.printStackTrace();
            }
        }
    }


    public static void main(String[] args) throws Exception {
        Server server = new Server(8000);
        server.workInCycle();
    }
}