package ru.track.prefork;

import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.ProtocolException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Map;
import java.util.Scanner;
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
        Scanner scanner = new Scanner(System.in);
        Thread adminThread = new Thread(() -> {
            while (true) {
                String command = scanner.nextLine();
                if (command.equals("list")) {
                    threadPool.forEach((atomicLong, serverThread) ->
                        log.info(serverThread.getName()));
                }
                else if (command.contains("drop ")) {
                    Long whomToDrop = Long.parseLong(command.substring(5));
                    if (threadPool.containsKey(whomToDrop)) {
                        threadPool.get(whomToDrop).interrupt();
                        IOUtils.closeQuietly(threadPool.get(whomToDrop).clientSocket);
                        log.info(threadPool.get(whomToDrop).getName() + " has disconnected");
                        threadPool.remove(whomToDrop);
                    }
                    else log.info("no client with such id");
                }
            }
        });
        adminThread.setName("adminThread");
        adminThread.start();

        ServerSocket serverSocket = null;
        try {
            serverSocket = new ServerSocket(9000, 10, InetAddress.getByName("localhost"));
            log.info("Server has started");
            while (!serverSocket.isClosed()) {
                final Socket clientSocket = serverSocket.accept();
                final Long userID = atomicID.getAndIncrement();
                ServerThread serverThread = new ServerThread(clientSocket, protocol, userID);
                log.info("Got new client from port " + clientSocket.getPort());
                threadPool.put(userID, serverThread);
                serverThread.start();
            }
        } catch (IOException ioe) {
            log.error("Can't handle server");
            exit(-1);
        } finally {
            IOUtils.closeQuietly(serverSocket);
        }


    }

    class ServerThread extends Thread {
        private Socket clientSocket;
        Protocol<Message> protocol;
        OutputStream out = null;
        InputStream in = null;
        private User user;

        private class User {
            private Long userID;
            private String username;
            private boolean isNewClient;

            private User (Long userID, String username, boolean isNewClient) {
                this.userID = userID;
                this.username = username;
                this.isNewClient = isNewClient;
            }
        }

        public ServerThread(@NotNull Socket clientSocket, Protocol<Message> protocol, Long atomicID) throws IOException {
            this.protocol = protocol;
            this.clientSocket = clientSocket;
            this.user = new User(atomicID, null, true);
            String address = clientSocket.getLocalAddress().toString().replaceAll("/", "");
            setName(String.format("Client[%d]@%s:%d",
                    atomicID,
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
                while (!this.isInterrupted()) {
                    if (user.isNewClient) {
                        send(new Message("Enter your name"));
                        in.read(buffer);
                        Message fromClient = protocol.decode(buffer);
                        if (!fromClient.text.isEmpty()) {
                            send(new Message(("Welcome to chat, " + fromClient.text)));
                            user.username = fromClient.text;
                            sendAsBroadcast(new Message((user.username +" has joined the chat")));
                            user.isNewClient = false;
                        }
                    }
                    nRead = in.read(buffer);
                    if (nRead != 0 && nRead != -1) {
                        Message fromClient = new Message(protocol.decode(buffer).text, user.username);
                        log.info(fromClient.toString());
                        if (fromClient.text.equalsIgnoreCase("exit")) {
                            send(fromClient);
                            sendAsBroadcast(new Message((fromClient.username + " has left the chat room")));
                            threadPool.get(user.userID).interrupt();
                            IOUtils.closeQuietly(threadPool.get(user.userID).clientSocket);
                            threadPool.get(user.userID).out.close();
                            threadPool.get(user.userID).in.close();
                            log.info(threadPool.get(user.userID).getName().toString() + " has disconnected");
                            threadPool.remove(user.userID);
                            break;
                        }
                        else sendAsBroadcast(fromClient);
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

        private void sendAsBroadcast (Message message) throws IOException {
            threadPool.forEach((atomicLong, serverThread) -> {
                if (serverThread.user.userID != this.user.userID)
                    try {
                        serverThread.send(message);
                    } catch (IOException e) {
                        log.error("error broadcasting message");
                    }
            });
        }
    }

    public static void main(String[] args) throws Exception {
        Server server = new Server(9000, new BinaryProtocol<>());
        server.serve();
    }
}