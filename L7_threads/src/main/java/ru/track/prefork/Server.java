package ru.track.prefork;

import org.apache.commons.io.IOUtils;
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
public class Server {
    static Logger log = LoggerFactory.getLogger(Server.class);
    private int port;

    public Server(int port) {
        this.port = port;
    }


    public void serve() throws IOException {

        ServerSocket ssock = null;
        try {
            ssock = new ServerSocket(port, 10, InetAddress.getByName("localhost"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        Socket sock = null;
        try {

            sock = ssock.accept();

            log.info("Accepted: " + sock.getPort());

            OutputStream os = sock.getOutputStream();
            InputStream in = sock.getInputStream();

            Scanner scan = new Scanner(in);

            String line = scan.nextLine();

            log.info("Server recieved:" + line);

            os.write((line + "\n").getBytes());

        } catch (IOException e) {
            e.printStackTrace();
        }




    }

    public static void main(String[] args) throws IOException {
        Server myserv = new Server(9000);
        myserv.serve();
    }


}
