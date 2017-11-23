package ru.track.prefork;

import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.track.prefork.protocol.JsonProtocol;
import ru.track.prefork.protocol.Message;
import ru.track.prefork.protocol.Protocol;
import ru.track.prefork.protocol.ProtocolException;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicLong;


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

    public static void main(String[] args) {
        Server server = new Server(9000, new JsonProtocol());
        try {
            server.serve();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void serve() throws Exception {
        log.info("Server started!");
        ServerSocket serverSocket = new ServerSocket(port, 10, InetAddress.getByName("localhost"));
        while (!serverSocket.isClosed()) {
            final Socket socket = serverSocket.accept();
            final long workerId = serverCounter.getAndIncrement();
            Worker worker = new Worker(socket, protocol, workerId);
            workerMap.put(workerId, worker);
            worker.start();
        }
    }

    class Worker extends Thread {
        @NotNull
        final OutputStream out;
        @NotNull
        final InputStream in;
        @NotNull
        Protocol<Message> protocol;
        @NotNull
        private long id;
        @NotNull
        private Socket socket;

        public Worker(@NotNull Socket socket, @NotNull Protocol<Message> protocol, long id) throws IOException {
            this.socket = socket;
            this.protocol = protocol;
            this.id = id;
            setName(String.format("Client[%d]@%s:%d", id, socket.getInetAddress(), socket.getPort()));

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
                // throw new RuntimeException(e);
            }
        }

        private void send(Message message) {
            // write echo to client
            try {
                out.write(protocol.encode(message));
                out.flush();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ProtocolException e) {
                e.printStackTrace();
            }
        }

        private void handleSocket(Socket socket) throws IOException {
            try {
                while (!socket.isClosed()) {
//                    byte[] buffer = new byte[1024];
//                    int nbytes = in.read(buffer);
//                    if(nbytes == -1) {
//                        break;
//                    }

                    // print msg from client
                    Message msgFromClient = protocol.decode(in, Message.class);
                    msgFromClient.text = String.format("Client@%s:%d>%s", socket.getInetAddress(), socket.getPort(), msgFromClient.text);
                    // log.info("Message: " + msgFromClient);

                    workerMap.forEach((longId, worker) -> {
                        if (longId != id) {
                            worker.send(msgFromClient);
                        }
                    });

                    // condition to close connection
                    if (msgFromClient.text.equals("exit")) {
                        break;
                    }
                }
            } catch (IOException e) {
                log.error("IOException", e);
            } catch (ProtocolException e) {
                e.printStackTrace();
            } finally {
                try {
                    in.close();
                    out.close();
                    socket.close();
                } catch (IOException e) {
                    log.error("Can't close client socket. " + e);
                }
                log.info("Client disconnected");
            }
        }
    }
}
