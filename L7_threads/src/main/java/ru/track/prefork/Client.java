package ru.track.prefork;

import java.io.*;
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

    public static void main(String[] args) {
        Client client = new Client(9876, "localhost");
        client.loop();
    }

    private void loop() {
        try (
                Socket echoSocket = new Socket(host, port);
                PrintWriter out =
                        new PrintWriter(echoSocket.getOutputStream(), true);
                BufferedReader in = new BufferedReader(
                        new InputStreamReader(echoSocket.getInputStream()));
                BufferedReader stdIn = new BufferedReader(
                        new InputStreamReader(System.in));
                ) {
            String userInput;
            while ((userInput = stdIn.readLine()) != null) {
                out.println(userInput);
                if (userInput.equals("exit")) break;
                System.out.printf("Echo: %s%n", in.readLine());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
