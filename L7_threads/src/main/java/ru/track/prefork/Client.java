package ru.track.prefork;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 *
 */
public class Client {
    public static Logger log = LoggerFactory.getLogger(Client.class);
    private int port;
    private String host;
    private Protocol<Message> protocol;
    private ClientServerListener csl;
    private ClientActionListener cal;

    public Client(int port, String host, Protocol<Message> protocol) {
        this.port = port;
        this.host = host;
        this.protocol = protocol;
    }

    public void connect() {
        Socket socket = null;
        try {
            socket = new Socket(host, port);
            log.info("Connected.");
            csl = new ClientServerListener(socket);
            cal = new ClientActionListener(socket);
            csl.start();
            cal.start();
        } catch (IOException e) {
            log.error("Connection failed.");
        }
    }

    private void getMessagesService(Socket socket) throws IOException, ProtocolException, ServerByteProtocolException  {
        ServerByteProtocol serverByteProtocol = new ServerByteProtocol(socket);
        while (!Thread.currentThread().isInterrupted()) {
            log.info("Listening to server...");
            byte[] buffer = serverByteProtocol.read();
            System.out.print("From server: ");
            Message msg = protocol.decode(buffer);
            System.out.println(msg.getAuthor() + "> " + msg.getText());
        }
    }

    private void sendMessagesService(Socket socket) throws IOException, ProtocolException, ServerByteProtocolException {
        ServerByteProtocol serverByteProtocol = new ServerByteProtocol(socket);
        Scanner scan = new Scanner(System.in);
        while (!Thread.currentThread().isInterrupted()) {
            log.info("Reading line...");
            String line = scan.nextLine();
            log.info("Sending line...");
            serverByteProtocol.write(protocol.encode(new Message(System.currentTimeMillis(), line)));
            if (line.equals("exit")) break;
        }
    }

    public static void main(String... args) throws IOException {
        Client cl = new Client(8000, "localhost", new BinaryProtocol<>());
        cl.connect();
    }

    abstract class ClientHandler extends Thread {
        private Socket socket;

        public ClientHandler(Socket socket) {
            this.socket = socket;
        }

        public Socket getSocket() {
            return socket;
        }
    }

    class ClientServerListener extends ClientHandler {

        public ClientServerListener(Socket socket) {
            super(socket);
        }

        @Override
        public void run() {
            Thread.currentThread().setName("ClientServer");
            try {
                getMessagesService(getSocket());
            } catch (IOException e) {
                log.error(e.getClass().getName() + ": " + e.getMessage());
            } catch (ProtocolException e) {
                log.error(e.getClass().getName() + ": " + e.getMessage());
            } catch (ServerByteProtocolException e) {
                log.error(e.getClass().getName() + ": " + e.getMessage());
            } finally {
                IOUtils.closeQuietly(getSocket());
                cal.interrupt();
                log.info("Connection closed.");
            }
        }
    }

    class ClientActionListener extends ClientHandler {

        public ClientActionListener(Socket socket) {
            super(socket);
        }

        @Override
        public void run() {
            Thread.currentThread().setName("ClientAction");
            try {
                sendMessagesService(getSocket());
            } catch (IOException e) {
                log.error(e.getClass().getName() + ": " + e.getMessage());
            } catch (ProtocolException e) {
                log.error(e.getClass().getName() + ": " + e.getMessage());
            } catch (ServerByteProtocolException e) {
                log.error(e.getClass().getName() + ": " + e.getMessage());
            }
            if (!getSocket().isClosed() && !isInterrupted()) {
                cal = new ClientActionListener(getSocket());
                cal.start();
            }
        }
    }
}
