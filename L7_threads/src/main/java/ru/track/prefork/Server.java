package ru.track.prefork;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sun.nio.ch.IOUtil;

import java.io.*;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

/**
 *
 */
public class Server {
    public static Logger log = LoggerFactory.getLogger(Server.class);

    private int port;

    public Server(int port) {
        this.port = port;
    }

    public void serve() {
        ServerSocket serverSocket = null;
        try {
            serverSocket = new ServerSocket(port, 10, InetAddress.getByName("localhost"));
        } catch (IOException e) {
            System.out.print("host is not valid");
        }
        while (true) {
            Socket socket = null;
            try {
                socket = serverSocket.accept();
                InputStream input = socket.getInputStream();
                OutputStream output = socket.getOutputStream();

                log.info("Client accepted");
                log.info("reading line");

                String line;
                byte[] buffer = new byte[1024];
                int nRead = input.read(buffer);
                while(nRead != -1) {
                    line = new String(buffer, 0, nRead);
                    if (line.equals("exit")) break;
                    log.info("line: " + line);
                    log.info("writing");
                    output.write(line.getBytes());
                    output.flush();
                    nRead = input.read(buffer);
                }


            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                IOUtils.closeQuietly(socket);
            }
            log.info("connection closed!");
        }
    }

    public static void main(String... args) {
        Server server = new Server(8000);
        server.serve();
    }
}
