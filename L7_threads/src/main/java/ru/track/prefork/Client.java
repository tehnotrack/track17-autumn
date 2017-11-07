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
    Socket clientSocket;

    public Client(int port, String host) throws IOException {
        this.port = port;
        this.host = host;
        clientSocket = new Socket(host, port);
    }
    public static String read(Socket clientSocket) throws IOException{
        InputStream reader = clientSocket.getInputStream();
        int len = 0;
        StringBuilder resp = new StringBuilder();
        while (len != -1) {
            byte[] query = new byte[1024];
            len = reader.read(query);
            if (len != -1) {
                byte [] subArr = Arrays.copyOfRange(query, 0, len);
                String buf = new String(subArr);
                resp.append(buf);
            }
        }
        return resp.toString();
    }

    public void connect() throws IOException {
        OutputStream writer = clientSocket.getOutputStream();
        Scanner in = new Scanner(System.in);
        String str = in.nextLine();
        writer.write(str.getBytes());
        writer.flush();
        clientSocket.shutdownOutput();

        String resp = read(clientSocket);
        System.out.println("Server answered: " + resp);
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
