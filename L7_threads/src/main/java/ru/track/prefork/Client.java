package ru.track.prefork;

import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;

/**
 *
 */
public class Client {
    private int port;
    private String host;

    public Client(@NotNull String host, int port) {
        this.port = port;
        this.host = host;
    }

    private void init() {
        try (Socket socket = new Socket(host, port)) {

            Thread consoleThread = new Thread(() -> {
                String line;
                try (
                        Scanner scanner = new Scanner(System.in);
                        PrintWriter pw = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()), true);
                        BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()))
                ) {
                    while (true) {
                        System.out.println("Print text:");

                        line = scanner.nextLine();

                        if (line.equals("exit")) {
                            System.out.println("Connection closed");
                            break;
                        }

                        pw.println(line);
                        String answer = br.readLine();
                        System.out.println("Answer from server: " + answer);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
            consoleThread.start();

            consoleThread.join();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }

    }

    public static void main(String[] args) throws Exception {
        final Client client = new Client("localhost", 8000);
        client.init();

    }

}
