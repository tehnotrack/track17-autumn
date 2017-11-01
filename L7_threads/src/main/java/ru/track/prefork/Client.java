package ru.track.prefork;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Arrays;
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

    public void loop() throws IOException {
        Socket socket = null;

        try {
            socket = new Socket(host, port);

            InputStream in = socket.getInputStream();
            OutputStream out = socket.getOutputStream();

            Scanner scanner = new Scanner(System.in);
            byte[] buffer = new byte[1024];

            while (true) {
                String line = scanner.nextLine();
                out.write(line.getBytes());
                out.flush();

                int nRead = in.read(buffer);
                String input = new String(buffer, 0, nRead);
                System.out.println(input);
                Arrays.fill(buffer, (byte) 0);

            }
        } catch (IOException e) {
            log.error("ERROR" + e.getMessage());
        } finally {
            if (socket != null)
                socket.close();
        }
    }

    public static void main(String[] args) throws Exception {
        final Client client = new Client(9000, "127.0.0.1");
        client.loop();
    }
}
