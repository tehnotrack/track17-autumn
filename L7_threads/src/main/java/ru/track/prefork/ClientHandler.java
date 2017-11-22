package ru.track.prefork;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ClientHandler implements Runnable {
    private String name;
    private Socket clientSocket;
    static Logger log = LoggerFactory.getLogger(ClientHandler.class);

    public ClientHandler(Socket clientSocket, int id) {
        this.clientSocket = clientSocket;
        name = String.format("Client[%d]@%s:%d",
                id, clientSocket.getInetAddress().toString().substring(1), clientSocket.getPort());
    }

    @Override
    public void run() {
        Thread.currentThread().setName(name);
        try (
                PrintWriter out =
                        new PrintWriter(clientSocket.getOutputStream(), true);
                BufferedReader in = new BufferedReader(
                        new InputStreamReader(clientSocket.getInputStream())
                )) {
            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                if (inputLine.equals("exit")) break;
                out.println(Thread.currentThread().getName() + ": " + inputLine);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
