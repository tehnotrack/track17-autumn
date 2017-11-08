package ru.track.prefork;

import java.awt.image.ImagingOpException;
import java.io.*;
import java.net.Socket;
import java.util.Arrays;
import java.util.Scanner;

/**
 *
 */
public class Client {
    private int port;
    private String host;
    private Socket clientSocket;

    private Client(int port, String host) throws IOException {
        this.port = port;
        this.host = host;
        clientSocket = new Socket(host, port);
    }



    public void connect() throws IOException {
        Scanner in = new Scanner(System.in);
        String currStr = in.nextLine();
        OutputStream writer = clientSocket.getOutputStream();
        while (!currStr.equals("exit")) {
            writer.write(currStr.getBytes());
            writer.flush();
            currStr = in.nextLine();
        }

        clientSocket.close();
    }

    public static void main(String[] args) {
        try {
            Client c = new Client(8080, "localhost");
            c.connect();
        }
        catch (IOException exc) {
            System.out.println(exc.getMessage());
        }
    }
}
