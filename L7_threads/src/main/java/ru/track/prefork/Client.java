package ru.track.prefork;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.ConnectException;
import java.net.Socket;
import java.net.SocketException;
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

    class Mythread extends Thread {
        InputStream in;

        Mythread(InputStream in) {
            this.in = in;
        }

        @Override
        public void run() {
            byte[] buffer = new byte[1024];
            try {
                while (!isInterrupted()) {
                    try {
                        int nRead = in.read(buffer);
                        if (nRead < 0)
                        {
                            log.error("Server died");
                            break;
                        }
                        log.info("Server:" + new String(buffer, 0, nRead));
                    }
                    catch (SocketException e) {
                    }
                }
            }
             catch (IOException e) {

            }
            try {
                in.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void loop() throws IOException
    {
        try {
            Socket socket = new Socket(host, port);
            OutputStream out = socket.getOutputStream();
            InputStream in = socket.getInputStream();
            Mythread write = new Mythread(in);
            Scanner scanner = new Scanner(System.in);
            String line;

            write.start();
            while (scanner.hasNextLine())
            {
                line = scanner.nextLine();
                if (!line.isEmpty()) {
                    if (line.equals("exit")){
                        log.info("You finished your session");
                        break;
                    }
                    out.write(line.getBytes());
                    out.flush();
                }
            }
            write.interrupt();
            socket.close();
        }
        catch (ConnectException e)
        {
            log.error("Cant connect");
        }
        catch (SocketException e)
        {
        }
    }

    public static void main(String args[]) throws IOException {
        Client client = new Client(9000, "localhost");
        client.loop();
    }
}
