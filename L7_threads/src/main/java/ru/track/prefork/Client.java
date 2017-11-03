package ru.track.prefork;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ConnectException;
import java.net.Socket;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

/**
 *
 */
public class Client {
    private int port;
    private String host;
    private static final int MAX_COUNT = 10;

    public Client(int port, String host) {
        this.port = port;
        this.host = host;
    }

    public void connect() throws IOException, InterruptedException {
        Socket socket = new Socket(host, port);

        OutputStream outputStream = socket.getOutputStream();
        InputStream inputStream = socket.getInputStream();

        byte[] response = new byte[MAX_COUNT];

        Scanner scanner = new Scanner(System.in);
        String message;
        while (!(message = scanner.nextLine()).equals("exit")) {
            if (message.isEmpty()) {
                continue;
            }

            if (message.length() > MAX_COUNT) {
                System.out.println("The message is too long. Try one more time.");

                continue;
            }
            
            outputStream.write(message.getBytes());

            int responseSize = inputStream.read(response);

            System.out.println(new String(response, 0, responseSize));
        }
    }

    public static void main(String[] args) {
        try {
            Client client = new Client(8080, "127.0.0.1");
            client.connect();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
}
