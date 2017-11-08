package ru.track.prefork;

import org.apache.commons.io.IOUtils;

import java.io.IOException;
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

    private void listen(OutputStream os, InputStream is) {
        try {
            byte[] buffer = new byte[4096];
            int nRead;
            while (true) {
                nRead = is.read(buffer);
                if (nRead == -1) {
                    break;
                }
                System.out.println(new String(buffer, 0, nRead));
            }
        }
        catch(IOException e) {}
        finally {
            IOUtils.closeQuietly(os);
            IOUtils.closeQuietly(is);
            System.out.println("Соединение с сервером разорвано");
        }
    }

    private void send(OutputStream os, InputStream is) {
        try {
            Scanner in = new Scanner(System.in);
            String str;
            while (true) {
                str = in.nextLine();
                if (str.equals("exit")) break;
                if (str.length() > 0) {
                    os.write(str.getBytes());
                    os.flush();
                }
            }
        }
        catch(IOException e) {}
        finally{
            IOUtils.closeQuietly(os);
            IOUtils.closeQuietly(is);
        }
    }

    public void start() throws IOException {
        Socket sock = null;
        Thread t;
        try {
            sock = new Socket(host, port);
            final OutputStream os = sock.getOutputStream();
            final InputStream is = sock.getInputStream();
            t = new Thread(() -> {send(os, is);});
            t.setDaemon(true);
            t.start();
            listen(os, is);
        }
        finally {
            IOUtils.closeQuietly(sock);
        }
    }
    public static void main(String[] args) throws Exception {
        final Client client = new Client(8100,"localhost");
        client.start();
    }
}
