package ru.track.prefork;

import com.google.gson.Gson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.SocketException;
import java.util.Scanner;

/**
 *
 */
public class Client {
    private static final int MAX_SIZE = 1024;
    private static Logger logger = LoggerFactory.getLogger("logger");

    private int port;
    private String host;

    private String username;

    public Client(int port, String host) {
        this.port = port;
        this.host = host;

        Scanner scanner = new Scanner(System.in);
        String  username;

        while (true) {
            System.out.print("Enter your username: ");

            username = scanner.next();

            if (Character.isLetter(username.charAt(0))) {
                this.username = username;

                break;
            } else {
                System.out.println("Username must start with letter");
            }
        }

        System.out.println("Thank you! You have logged in...");
    }

    public void connect() throws IOException, InterruptedException {
        Socket socket = new Socket(host, port);

        logger.info("Connected to server on host " + host + " and port " + port);

        Thread handleServer = new Thread(() -> {
            try {
                handleServer(socket);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        handleServer.setDaemon(true);
        handleServer.setName("Getter");
        handleServer.start();

        Thread handleClient = new Thread(() -> {
            try {
                handleClient(socket);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        handleClient.setName("Sender");
        handleClient.start();
    }

    private void handleServer(Socket socket) throws IOException {
        byte[] byteMessage = new byte[MAX_SIZE];
        int size;

        InputStream inputStream = socket.getInputStream();

        while ((size = inputStream.read(byteMessage)) != -1) {
            String text = new String(byteMessage, 0, size);

            Gson gson = new Gson();
            Message message = gson.fromJson(text, Message.class);
    
            if (message.getUsername().equals("exit") && message.getText().equals("exit")) {
                String msg;
                System.out.println(msg = "Connection lost with the server!");
                logger.info(msg);
        
                System.exit(0);
            }

            String infoMessage = "Got from " + message.getUsername() + ": " + message.getText();
            logger.info(infoMessage);
            System.out.println(infoMessage);
        }
    }

    private void handleClient(Socket socket) throws IOException {
        Scanner scanner = new Scanner(System.in);
        String text;

        while (!(text = scanner.nextLine()).equals("exit")) {
            if (text.isEmpty()) {
                continue;
            }

            if (text.length() > MAX_SIZE) {
                System.out.println("The message is too long. Try one more time.");

                continue;
            }

            Message message = new Message(username, text, System.currentTimeMillis());
            Gson gson = new Gson();
            String convertedMessage = gson.toJson(message);

            OutputStream outputStream = socket.getOutputStream();

            try {
                outputStream.write(convertedMessage.getBytes());
            } catch (SocketException e) {
                System.out.println("Connection lost with the server!");

                logger.info("Connection lost");

                break;
            }

            logger.info("Sent message " + text);
        }
    }

    public static void main(String[] args) {
        try {
            Client client = new Client(8000, "127.0.0.1");
            client.connect();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
}
