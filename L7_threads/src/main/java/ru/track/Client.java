package ru.track;

import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sun.rmi.runtime.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Queue;
import java.util.Scanner;

public class Client {
    private String host;
    private int port;
    private final Socket socket;
    private Thread receiver;
    private Thread sender;
    private static Logger logger = LoggerFactory.getLogger("Client");

    public Client(@NotNull String host, int port) throws IOException {
        this.host = host;
        this.port = port;
        socket = new Socket(host, port);

    }

    private void shutdown() throws IOException {
        if (socket != null)
            socket.close();
    }

    public static void main(String[] args) throws IOException {
        Client client = new Client(args[0], Integer.parseInt(args[1]));

        client.sender = new Thread() {
            @Override
            public void run() {
                Scanner sc = new Scanner(System.in);
                String input;
                while ((input = sc.nextLine()) != null) {
                    try {
                        PrintWriter out = new PrintWriter(client.socket.getOutputStream(), true);
                        out.println(input);
                        if (input.equals("exit")) {
                            client.shutdown();
                            client.receiver.interrupt();
                            break;
                        }
                    } catch (IOException e) {
                        logger.error("Troubles in connection: {}", e);
                        break;
                    }
                }
            }
        };

        client.receiver = new Thread() {
            @Override
            public void run() {
                try {
                    BufferedReader in = new BufferedReader(new InputStreamReader(client.socket.getInputStream()));
                    while (!currentThread().isInterrupted())
                    {
                        if (in.ready())
                            logger.info("Got response from server: {}", in.readLine());
                    }
                } catch (IOException e) {
                    logger.error("Troubles in connection: {}", e);
                }
            }
        };

        client.sender.start();
        client.receiver.start();
    }
}
