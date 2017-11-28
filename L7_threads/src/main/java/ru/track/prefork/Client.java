package ru.track.prefork;

import java.io.*;
import java.net.ConnectException;
import java.net.Socket;
import java.net.SocketException;
import java.util.Scanner;

public class Client {
    private int port;
    private String host;

    public Client(int port, String host) {
        this.port = port;
        this.host = host;

        try(Socket socket = new Socket(host, port);
            DataInputStream in = new DataInputStream(socket.getInputStream());
            DataOutputStream out = new DataOutputStream(socket.getOutputStream())) {

            System.out.println("Successfully connected to " + host);
            System.out.println("Please enter a string: ");
            Scanner scanner = new Scanner(System.in);
            String s = scanner.nextLine();
            while(s.equals("")) {
                System.out.println("Please enter a string: ");
                s = scanner.nextLine();
            }
            out.writeUTF(s);
            out.flush();

            String msg = in.readUTF();
            System.out.println("Echo from server: " + msg);
        }
        catch(ConnectException e) {
            System.err.println("Server is not available now!");
        }
        catch(SocketException e) {
            System.err.println("Connection reset!");
        }
        catch(IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String args[]) {
        Client client = new Client(8000, "127.0.0.1");
    }
}
