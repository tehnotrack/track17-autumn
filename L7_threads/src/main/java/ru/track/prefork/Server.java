package ru.track.prefork;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;


public class Server {
    private int port;
    private int clientId = 0;

    public Server(int port) {
        this.port = port;
        try(ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("Server is started!");
            while(true) {
                System.out.println("Waiting for connection...");
                Socket client = serverSocket.accept();
                ConnectionThread thread = new ConnectionThread(client);
                String name = "Client[" + clientId + "]@[" + client.getInetAddress() + "]:[" +
                        client.getPort() + "]";
                thread.setName(name);
                clientId++;
                thread.start();
                System.out.println("Client " + thread.getName() + " connected!");
            }
        }
        catch(IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String args[]) {
        Server server = new Server(8000);
    }
}

