package ru.track.prefork;

import java.io.*;
import java.net.ConnectException;
import java.net.Socket;
import java.util.Scanner;


/**
 *
 */
public class Client {

    private int port;

    Client(int port) {
       this.port = port;
    }

    public void runing() throws IOException, ClassNotFoundException {
        BinaryProtocol<Message> protocol = new BinaryProtocol<>();
        int srvMsg = 0;
        String str;
        Socket socket = null;
        byte[] msg = new byte[1024];
        try {
            socket = new Socket("localhost", port);
            System.out.println("client started");
            try (InputStream in = socket.getInputStream()) {
                ThreadSample t = new ThreadSample(socket);
                t.setDaemon(true);
                t.start();
                while (!socket.isOutputShutdown()) {
                    srvMsg = in.read(msg);
                    if (srvMsg != -1) {
                        Message message = (Message)protocol.decode(msg);
                        str = message.getData();
                        //str = new String(msg, 0, srvMsg);
                        if (str.equals("exit")) {
                            System.err.println("You were disconected from server");
                            t.interrupt();
                            break;
                        }
                        System.out.println(str);
                    } else {
                        System.err.println("Server was shut down");
                        t.interrupt();
                        break;
                    }
                }
            } catch (IOException e) {

            }
        } catch (ConnectException e) {
            System.err.println("Server does not response");
        }finally {
            try {
                if (socket != null)
                socket.close();
            } catch (IOException e) {
                System.err.println("Socket not closed");
            }
        }
    }


    public static void main(String[] args) throws IOException, ClassNotFoundException {
        Client c = new Client(8100);
        c.runing();
    }
}


class ThreadSample extends Thread {
    Socket socket;
    BinaryProtocol<Message> protocol = new BinaryProtocol<>();
    ThreadSample (Socket s) {
        this.socket = s;
    }
    @Override
    public void run() {
        try (OutputStream out = socket.getOutputStream();
             BufferedReader br = new BufferedReader(
                     new InputStreamReader(System.in))){
            String str;
            Message message;
            while (true) {
                    str = br.readLine();
                    if (!str.equals("")) {
                        if (!isInterrupted()) {
                            message = new Message(str);
                            out.write(protocol.encode(message));
//                            out.write(str.getBytes());
                            out.flush();
                        }
                        if (str.equals("exit")) {
                            break;
                        }
                    }
            }
        } catch (IOException e) {}
    }
}
