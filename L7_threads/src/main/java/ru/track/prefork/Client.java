package ru.track.prefork;

import java.io.*;
import java.net.Socket;

/**
 *
 */
public class Client {
    private int port;
    private String host;
    private Socket socket;

    public Client(int port, String host) {
        this.port = port;
        this.host = host;
        if(runClient() == 0) {
            clientHandler();
        }
    }

    private int runClient() {
        try {
            System.out.println("[main]: Try to connect to server.");
            socket = new Socket(this.host, this.port);
            System.out.println("[main]: Successfully connected.");
        } catch (IOException e) {
            System.out.println("[main]: Error while connection.");
            return(-1);
        }
        return 0;
    }

    private void clientHandler() {
        System.out.println("[main]: Try to send HELO:");
        try (
            BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
            DataOutputStream oos = new DataOutputStream(socket.getOutputStream());
            DataInputStream ois = new DataInputStream(socket.getInputStream())
        ) {
            Thread readerThread = new Thread(() -> {
                    while(!socket.isOutputShutdown() && !Thread.interrupted()) {
                        try {
                            System.out.println(Thread.currentThread().getName() + ": Reading...");
                            String response = ois.readUTF();
                            System.out.println(Thread.currentThread().getName() + ": Response read.");
                            System.out.println(Thread.currentThread().getName() + ": I have read: " + response);
                        } catch (IOException e) {
                            System.out.println(Thread.currentThread().getName() + ": Can't read.");
                            break;
                        }
                    }
            });

            readerThread.start();

            while(!socket.isOutputShutdown()) {
                if(br.ready()) {
                    String data = br.readLine();
                    oos.writeUTF(data);
                    oos.flush();
                    if(data.equalsIgnoreCase("exit")) {
                        readerThread.interrupt();
                        break;
                    }
                }
            }

        } catch (IOException e) {
            System.out.println("[main]: Can't send HELO.");
        }
    }

    public static void main(String[] args) throws Throwable {
        Client client = new Client(8080, "localhost");
    }
}
