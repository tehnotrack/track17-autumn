package ru.track.prefork;

//import com.sun.tools.jdeprscan.scan.Scan;
//import com.sun.org.apache.xpath.internal.operations.String;
import org.apache.commons.io.IOUtils;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.track.workers.NioClient;

import java.io.*;
import java.net.Socket;
import java.security.Signature;
import java.util.Scanner;

/**
 *
 */
public class Client {
    static Logger log = LoggerFactory.getLogger(NioClient.class);
    private int port;
    private String host;

    public Client(@NotNull String host, int port) {
        this.host = host;
        this.port = port;
    }


    public void loop() throws Exception {
        Socket socket = new Socket(java.lang.String.valueOf(host), port);
        Scanner scanner = new Scanner(System.in);

        try (
                InputStream in = socket.getInputStream();
                OutputStream out = socket.getOutputStream();
                ) {
            while (true) {
                String line = scanner.nextLine();
                out.write(line.getBytes());
                out.flush();
                byte[] buffer = new byte[2048];
                int nByte = in.read(buffer);

                System.out.println(new String(buffer, 0, nByte));
            }
        }
        finally {
            socket.close();
        }

    }

    public static void main(String[] args) throws Exception {
        final Client client = new Client("localhost", 9000);
        client.loop();
    }
}
