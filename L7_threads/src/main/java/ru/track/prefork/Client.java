package ru.track.prefork;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;


/**
 *
 */
public class Client {

    private int port;

    Client(int port) {
       port = port;
    }

    public void runing() throws IOException {
        int srvMsg = 0;
        String str;
        Socket socket = null;
        byte[] msg = new byte[1024];
        try {
            socket = new Socket("localhost", port);
            System.out.println("client started");
            try (InputStream in = socket.getInputStream()) {
                ThreadSample t = new ThreadSample(socket);
                t.start();
                while (!socket.isOutputShutdown()) {
                    srvMsg = in.read(msg);
                    if (srvMsg != -1) {
                        str = new String(msg, 0, srvMsg);
                        if (str.equals("exit")) {
                            System.err.println("You were disconected from server");
                            t.interrupt();
                            break;
                        }
                        System.out.println(new String(msg, 0, srvMsg));
                    } else {
                        System.err.println("Server was shut down");
                        t.interrupt();
                        break;
                    }
                }
            } catch (IOException e) {

            }
        } finally {
            try {
                socket.close();
            } catch (IOException e) {
                System.err.println("Socket not closed");
            }
        }
    }


    public static void main(String[] args) throws IOException {
        Client c = new Client(8100);
        c.runing();
    }
}


class ThreadSample extends Thread {
    Socket socket;
    ThreadSample (Socket s) {
        this.socket = s;
    }
    @Override
    public void run() {
        try (OutputStream out = socket.getOutputStream();
             BufferedReader br = new BufferedReader(
                     new InputStreamReader(System.in))){
            String str;
            while (!isInterrupted()) {
                while (!br.ready())
                    sleep(1000);
                    str = br.readLine();
                    if (!str.equals("")) {
                        if (!isInterrupted())
                            out.write(str.getBytes());
                        if (str.equals("exit"))
                            break;
                    }
            }
        } catch (IOException | InterruptedException e) {}
    }
}
