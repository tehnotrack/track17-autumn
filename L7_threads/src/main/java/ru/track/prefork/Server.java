package ru.track.prefork;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;


/**
 *
 */
public class Server {
    private int port;
    static Logger log = LoggerFactory.getLogger(Server.class);

    public Server(int port) {
        this.port = port;
    }

    public void serve() {
        try (
                ServerSocket listener = new ServerSocket(port, 10, InetAddress.getByName("localhost"));
                Socket clientSocket = listener.accept();
                PrintWriter out =
                        new PrintWriter(clientSocket.getOutputStream(), true);
                BufferedReader in = new BufferedReader(
                        new InputStreamReader(clientSocket.getInputStream())
                )
                ) {
            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                if (inputLine.equals("exit")) break;
                out.println(inputLine);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        // log.info("Client: " + new String(buffer, 0, nRead));
    }

    public static void main(String[] args) {
        Server server = new Server(9876);
        server.serve();
    }
}
