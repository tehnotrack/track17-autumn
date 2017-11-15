package ru.track.prefork;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.ws.ProtocolException;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Map;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicLong;


/**
 *
 */
public class Server {
    private int port;
    private boolean isRunning = false;
    private ServerSocket socket;
    private AtomicLong counter = new AtomicLong(0);
    private ConcurrentHashMap<Long, Worker> threadMap;
    private Protocol<MyMessage> protocol = new BinaryProtocol<MyMessage>();
    private Logger log;


    private Server(int port) {
        log = LoggerFactory.getLogger(Server.class);
        log.info("Try to start server");
        this.port = port;
        this.isRunning = true;
        this.threadMap = new ConcurrentHashMap<>();
        if (startServer() == 0) {
            listenLoop();
        }
    }

    private int startServer() {
        try {
            this.socket = new ServerSocket(this.port);
            log.info("Server started at port " + this.port + ".");
        } catch (Exception e) {
            log.error("Can't start socket. Error: " + e.getLocalizedMessage());
            return (-1);
        }

        log.info("Start admin thread");



            Thread adminThread = new Thread(() -> {
                try(BufferedReader br = new BufferedReader(new InputStreamReader(System.in))) {
                    while (isRunning) {
                        if (br.ready()) {
                            String data = br.readLine();
                            log.info("Admin write: " + data);
                            if (data.equalsIgnoreCase("list")) {
                                for (Map.Entry<Long, Worker> entry : threadMap.entrySet()) {
                                    System.out.println(entry.getValue().getName());
                                }
                            }

                        }

                    }
                } catch (IOException e) {
                    log.error("Can't read from admin console: " + e.getLocalizedMessage());
                }
            });
            adminThread.setName("admin");
            adminThread.start();
        return 0;
    }

    private void listenLoop() {
        while (isRunning) {
            try {
                log.info("Wait for connection");
                Socket client = socket.accept();
                log.info("Connection accepted. Try to create new handler.");
                Worker handler = new Worker(client, counter.getAndIncrement());
                handler.start();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

    public class Worker extends Thread {

        private Socket socket;
        private Long id;
        private InputStream in;
        private OutputStream out;

        public Worker(Socket socket, Long id) {
            log.info("Handler started");
            this.socket = socket;
            this.id = id;
            setName(String.format("Client%d@%s:%d", id,
                    socket.getInetAddress().toString(),
                    socket.getPort()));
            try {
                this.in = socket.getInputStream();
                this.out = socket.getOutputStream();
            } catch (IOException e) {
                log.info("Can't get IO streams");
                interrupt();
            }

        }

        public void sendMessage(String message) {
            try {
                MyMessage msg = new MyMessage(System.currentTimeMillis() / 1000L, message);
                out.write(protocol.encode(msg));
                out.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void run() {

            try {
                threadMap.put(id, (Worker) Thread.currentThread());

                byte[] buf = new byte[1024];


                while (!socket.isClosed() && !interrupted()) {
                    int nRead = in.read(buf);
                    if (nRead != -1) {
                        try {
                            MyMessage data = protocol.decode(buf);
                            log.info("Client write: " + data.text + ".");
                            if (data.text.equals("exit")) {
                                break;
                            }

//                            EHO Server:
//                            data.text = ">" + data.text;
//                            System.out.println(Thread.currentThread().getName() + ": " + data.text);

//                            out.write(protocol.encode(data));
//                            out.flush();
//                            log.info("Sent: \"" + data.text + "\" to client.");

//                            BroadCast server:
                            System.out.println(Thread.currentThread().getName() + ": >" + data.text);

                            for (Map.Entry<Long, Worker> entry : threadMap.entrySet()) {
                                if (entry.getKey().equals(id)) {
                                    continue;
                                }
                                entry.getValue().sendMessage(data.text);
                            }

                        } catch (ProtocolException e) {
                            log.error("Error with encoding/decoding: " + e.getLocalizedMessage());
                        }
                    }
                }
                log.info("Close connection");
                threadMap.remove(id);

                out.close();
                in.close();
                socket.close();
            } catch (IOException e) {
                log.error("Error while new client acceptance: " + e.getLocalizedMessage());
            }
        }
    }


    public static void main(String[] args) throws Throwable {
        Server server = new Server(8080);
    }
}

