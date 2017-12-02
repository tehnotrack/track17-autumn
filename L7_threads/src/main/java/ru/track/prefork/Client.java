package ru.track.prefork;

import java.io.*;
import java.net.Socket;

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
                Socket sock = new Socket(host, port);
                BufferedReader in = new BufferedReader(
                        new InputStreamReader(sock.getInputStream()))
        ) {
            Thread consReader = new ConsoleReader(sock);
            Thread servListener = new ServerListener(sock);
            servListener.start();
            consReader.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

class ConsoleReader extends Thread {
    private Socket sock;

    public ConsoleReader(Socket sock) {
        this.sock = sock;
    }

    @Override
    public void run() {
        try (BufferedReader stdIn = new BufferedReader(
                new InputStreamReader(System.in));
             PrintWriter out =
                     new PrintWriter(sock.getOutputStream(), true)) {
            String userInput;
            while ((userInput = stdIn.readLine()) != null) {
                out.println(userInput);
                if (userInput.equalsIgnoreCase("exit")) break;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}


class ServerListener extends Thread {
    private Socket sock;

    public ServerListener(Socket sock) {
        this.sock = sock;
    }

    @Override
    public void run() {
        try (BufferedReader in = new BufferedReader(
                new InputStreamReader(sock.getInputStream()))) {
            while (true) {
                System.out.printf("Echo from %s%n", in.readLine());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}