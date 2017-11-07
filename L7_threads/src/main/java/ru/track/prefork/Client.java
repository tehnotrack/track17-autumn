package ru.track.prefork;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.charset.Charset;
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
        Socket socket = null;
        Logger log = LoggerFactory.getLogger(Server.class);
        BufferedReader fromKeyboard = new BufferedReader(new InputStreamReader(System.in));
        try {
            socket = new Socket(host, port);
        } catch (UnknownHostException uhe) {
            log.error ("cant open socket");
            System.exit(-1);
        }

        InputStream in = socket.getInputStream();
        OutputStream out = socket.getOutputStream();

        byte [] buffer = new byte [1024];
        String str;

        while (true){
            str = fromKeyboard.readLine();
            if (!str.equals("")) {
                out.write(str.getBytes());
                out.flush();
                int nRead = in.read(buffer);
                str = new String(buffer, 0, nRead);
                if (str.equals("exit")) {
                    out.close();
                    fromKeyboard.close();
                    in.close();
                    break;
                }
                else
                    System.out.println(str);
            }
            else;
        }
    }

    public static void main (String[] args) throws Exception {
        Client client = new Client(9000, "localhost");
        client.loop();
    }
}