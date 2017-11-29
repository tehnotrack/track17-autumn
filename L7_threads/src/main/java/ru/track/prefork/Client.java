package ru.track.prefork;

import com.sun.mail.iap.ByteArray;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.input.CharSequenceInputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
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
            OutputStream output = sock.getOutputStream();
            InputStream input = sock.getInputStream();
            Scanner scan = new Scanner(System.in);
            log.info("Connected.");
            while (true) {

                log.info("Reading line...");

                String line = scan.nextLine();

                log.info("Sending line...");

                output.write((line).getBytes());
                output.flush();

                log.info("Getting echo");

                byte[] buffer = new byte[1024];
                System.out.print("Echo: ");
                int nRead = input.read(buffer);
                System.out.print(new String(buffer, 0, nRead));
//                while (nRead != -1) {
//                    nRead = input.read(buffer);
//                }
                System.out.println();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            IOUtils.closeQuietly(sock);
        }
        log.info("Connection closed!");
    }

    public static void main(String... args) throws IOException {
        Client cl = new Client(8000, "localhost");
        cl.connect();
    }
}
