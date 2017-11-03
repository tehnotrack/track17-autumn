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
    private int port;
    private String host;

    public Client(int port, String host) {
        this.port = port;
        this.host = host;
    }

    private void connect() throws IOException {
        Socket socket = new Socket(host, port);

        OutputStream outputStream = socket.getOutputStream();
        InputStream inputStream = socket.getInputStream();

        byte[] response = new byte[1024];

        Scanner scanner = new Scanner(System.in);
        while (true) {
            String message = scanner.nextLine();

            outputStream.write(message.getBytes());
            inputStream.read(response);

            System.out.println(new String(response));
        }
    }

    public static void main(String[] args) {
        Client client = null;

        try {
            client = new Client(8080, "127.0.0.1");
            client.connect();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
