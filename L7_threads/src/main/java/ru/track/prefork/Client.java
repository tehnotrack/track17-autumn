package ru.track.prefork;

import com.sun.mail.iap.ByteArray;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Scanner;

/**
 *
 */
public class Client {
    public static Logger log = LoggerFactory.getLogger(Client.class);
    private int port;
    private String host;

    public Client(int port, String host) {
        this.port = port;
        this.host = host;
    }

    public void connect() {
        Socket sock = null;
        try {
            sock = new Socket(host, port);
            log.info("Connected. Reading line...");
            OutputStream output = sock.getOutputStream();
            Scanner scan = new Scanner(System.in);
            String line = scan.nextLine();
            log.info("Sending line...");
            output.write((line + "\n").getBytes());
            output.flush();

            log.info("Getting echo");
            InputStream input = sock.getInputStream();
            byte[] buffer = new byte[1024];
            int nRead = input.read(buffer);
            if (nRead == -1) {
                throw new IOException();
            }
            System.out.println("Echo: " + new String(buffer, 0, nRead));
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            IOUtils.closeQuietly(sock);
        }
        log.info("Connection closed!");
    }

    public static void main(String... args) {
        Client cl = new Client(8000, "localhost");
        cl.connect();
    }
}
