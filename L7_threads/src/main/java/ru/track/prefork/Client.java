package ru.track.prefork;

import java.awt.image.ImagingOpException;
import java.io.*;
import java.net.Socket;

/**
 *
 */
public class Client {
    private int port;
    private String host;
    Socket clientSocket;

    public Client(int port, String host) throws IOException {
        this.port = port;
        this.host = host;
        clientSocket = new Socket(host, port);
    }
    public void connect() throws IOException {
        OutputStream writer = clientSocket.getOutputStream();
        writer.write(111);
        InputStream reader = clientSocket.getInputStream();
        int res = 1;
        StringBuilder resp = new StringBuilder();
        while (res != -1) {
            byte[] query = new byte[1024];
            res = reader.read(query);
            resp.append(query);
        }
        System.out.println(resp.toString());
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
