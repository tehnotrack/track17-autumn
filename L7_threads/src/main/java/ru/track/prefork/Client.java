package ru.track.prefork;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Arrays;
import java.util.Scanner;

public class Client {
    static Logger log = LoggerFactory.getLogger(Client.class);
    private int port;
    private String host;

    public Client(int port, String host) {
        this.port = port;
        this.host = host;
    }

    public static void main(String[] args) {
        Client client = new Client(9000, "localhost");
        try {
            client.loop();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loop() throws Exception {
        System.out.println("Client started!");
        Socket socket = new Socket(host, port);
        InputStream in = socket.getInputStream();
        OutputStream out = socket.getOutputStream();

        Scanner scanner = new Scanner(System.in);

        try {
            while (true) {
                // read line
                System.out.println("Write msg for server:");
                String line = scanner.next();

                // write to socket
                out.write(line.getBytes());
                out.flush();

                // condition to close socket
                if (line.equals("exit")) {
                    socket.close();
                    return;
                }

                // read msg from server
                byte[] buffer = new byte[1024];
                int nRead = in.read(buffer);

                // print msg from server
                byte[] slicedBuffer = Arrays.copyOfRange(buffer, 0, nRead);
                String msgFromServer = new String(slicedBuffer);
                log.info("From server: " + msgFromServer);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            out.write("exit".getBytes());
            out.flush();
            socket.close();
        }
    }
}
