package ru.track.prefork;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Scanner;

/**
 *
 */
public class Client {
    private int port;
    private String host;

    public Client(int port, String host) {
        this.port = port;
        this.host = host;
    }

    public void loop() throws Exception {

        Socket socket = new Socket(host, port);
        InputStream in = socket.getInputStream();
        OutputStream out = socket.getOutputStream();

        Scanner scanner = new Scanner(System.in);

        while (true) {
            String line = scanner.nextLine();
            out.write(line.getBytes());
            out.flush();


        byte[] buff = new byte[1024];
        int reader = in.read(buff);
        System.out.println(new String(buff, 0, reader));
            }

    }

    public static void main(String[] args) throws Exception{
        Client client = new Client(9000,"localhost");
        client.loop();
    }

}
