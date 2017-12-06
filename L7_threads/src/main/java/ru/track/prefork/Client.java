package ru.track.prefork;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Scanner;

/**
 *
 */
public class Client {
    private static final int MAX_SIZE = 1024;

    private int port;
    private String host;

    public Client(int port, String host) {
        this.port = port;
        this.host = host;
    }

    public void connect() throws IOException, InterruptedException {
        Socket socket = new Socket(host, port);

        Thread handleServer = new Thread(() -> {
            try {
                handleServer(socket);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        handleServer.start();

        Thread handleClient = new Thread(() -> {
            try {
                handleClient(socket);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        handleClient.start();
    }

    private void handleServer(Socket socket) throws IOException {
        InputStream inputStream = socket.getInputStream();

        byte[] message = new byte[MAX_SIZE];
        int size;

        while ((size = inputStream.read(message)) != -1) {
            System.out.println("new message: " + new String(message, 0, size));
        }
    }

    private void handleClient(Socket socket) throws IOException {
        OutputStream outputStream = socket.getOutputStream();

        Scanner scanner = new Scanner(System.in);
        String message;
        while (!(message = scanner.nextLine()).equals("exit")) {
            if (message.isEmpty()) {
                continue;
            }

            if (message.length() > MAX_SIZE) {
                System.out.println("The message is too long. Try one more time.");

                continue;
            }

            outputStream.write(message.getBytes());
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
