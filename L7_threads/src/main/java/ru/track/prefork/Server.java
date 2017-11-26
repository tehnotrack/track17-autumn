package ru.track.prefork;

import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.ProtocolException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import static java.lang.System.exit;

public class Server {
    static Logger log = LoggerFactory.getLogger(Server.class);

    private Protocol<Message> protocol;
    private int port;

    public Server(int port, Protocol<Message> protocol) throws IOException {
        this.port = port;
        this.protocol = protocol;
    }

    private AtomicLong atomicID = new AtomicLong(0);
    private Map<Long, ServerThread> threadPool = new ConcurrentHashMap<>();

    public void serve() throws Exception {
        try {
            ServerSocket serverSocket = new ServerSocket(9000, 10, InetAddress.getByName("localhost"));
            log.info("Server has started");
            while (true) {
                final Socket clientSocket = serverSocket.accept();
                Long userID = atomicID.getAndIncrement();
                ServerThread serverThread = new ServerThread(clientSocket, protocol, userID);
                log.info("Got new client from port " + clientSocket.getPort());
                threadPool.put(userID, serverThread);
                serverThread.start();
            }
        } catch (IOException ioe) {
            log.error("Can't handle server");
            exit(-1);
        }
    }

    class ServerThread extends Thread {
        private Long userID;
        private Socket clientSocket;
        private Protocol<Message> protocol;
        OutputStream out = null;
        InputStream in = null;
        private boolean newClient;

        public ServerThread(@NotNull Socket clientSocket, Protocol<Message> protocol, Long atomicID) throws IOException {
            this.protocol = protocol;
            this.clientSocket = clientSocket;
            this.newClient = true;
            this.userID = atomicID;
            String address = clientSocket.getLocalAddress().toString().replaceAll("/", "");
            setName(String.format("Client[%d]@%s:%d",
                    userID,
                    address,
                    clientSocket.getPort()));
            try {
                in = clientSocket.getInputStream();
                out = clientSocket.getOutputStream();
            } catch (IOException e) {
                log.error("cant open stream to write/read");
            }
        }

        @Override
        public void run() {
            byte[] buffer = new byte[1024];
            int nRead;
            try {
                while (true) {
                    String username = null;
                    if (newClient) {
                        send(new Message("Enter your name"));
                        in.read(buffer);
                        Message fromClient = protocol.decode(buffer);
                        if (!fromClient.text.isEmpty()) {
                            send(new Message(("Welcome to chat, " + fromClient.text)));
                            username = fromClient.text;
                            newClient = false;
                        }
                    }
                    nRead = in.read(buffer);
                    if (nRead != 0 && nRead != -1) {
                        Message fromClient = new Message(protocol.decode(buffer).text, username);
                        log.info(fromClient.toString());
                        threadPool.forEach((atomicLong, serverThread) -> {
                            if (serverThread.userID != this.userID)
                                try {
                                    serverThread.send(fromClient);
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                        });
                    }
                }

            } catch (ProtocolException e) {
                log.error("cant decode message");
                return;
            } catch (IOException e) {
                log.error("cant read from buffer");
                return;
            }
        }

        private void send(Message message) throws IOException {
            try {
                out.write(protocol.encode(message));
                out.flush();
            } catch (IOException ex) {
                log.error("cant send message:" + message.text);
            }
        }
        //удалять клиента из мапы если поймали ошибку
    }

    public static void main(String[] args) throws Exception {
        Server server = new Server(9000, new BinaryProtocol<>());
        server.serve();
    }
}