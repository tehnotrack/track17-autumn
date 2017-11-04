package ru.track.prefork;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
            byte[] buff = new byte[1024];

            while (true) {
                String line = scanner.nextLine();
                out.write(line.getBytes());
                out.flush();
                int reader = in.read(buff);
                System.out.println(new String(buff, 0, reader));
            }
        } catch (IOException e) {
            log.error("ERROR" + e.getMessage());
        } finally {
            if (socket != null)
                socket.close();
        }
    }


    public static void main(String[] args) throws Exception{
        Client client = new Client(9000,"localhost");
        client.loop();
    }

}
