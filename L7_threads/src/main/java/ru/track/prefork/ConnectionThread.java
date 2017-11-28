package ru.track.prefork;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class ConnectionThread extends Thread {

    private Socket clientSocket;

    public ConnectionThread(Socket clientSocket) {
        this.clientSocket = clientSocket;
    }

    @Override
    public void run() {
        try(DataInputStream in = new DataInputStream(clientSocket.getInputStream());
            DataOutputStream out = new DataOutputStream(clientSocket.getOutputStream())) {

            System.out.println("Accepted: " + clientSocket.getInetAddress());

            String msg = in.readUTF();
            System.out.println("Message from client: " + this.getName() + " " + msg);
            out.writeUTF(msg);
            out.flush();

        }
        catch(IOException e) {
            System.err.println("Client " + this.getName() + " disconnected!");
        }
    }
}
