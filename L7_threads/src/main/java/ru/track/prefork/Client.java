package ru.track.prefork;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

/**
 *
 */
public class Client {
    private int port;
    private String host;
    static Logger log = LoggerFactory.getLogger(Client.class);

    public Client(int port, String host) {
        this.port = port;
        this.host = host;
    }

    public void loop() throws IOException
    {
        Socket socket = new Socket(host, port);

        OutputStream out = socket.getOutputStream();
        InputStream in = socket.getInputStream();
        byte[] buffer = new byte[1024];

        Scanner scanner = new Scanner(System.in);

        while(true)
        {
            String line = scanner.nextLine();
            out.write(line.getBytes());
            out.flush();
            int nRead = in.read(buffer);
            if (line.equals("exit") | (nRead < 0)){
                log.error("Ooops, breaked");
                break;
            }
            log.info("Server:" + new String(buffer, 0, nRead));
        }

        socket.close();
    }

    public static void main(String args[]) throws IOException {
        Client client = new Client(9000, "localhost");
        client.loop();
    }
}
