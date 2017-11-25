package ru.track.prefork;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

/**
 *
 */
public class Server {
    private int port;

    public Server(int port) {
        this.port = port;
    }

    private void init() {
        try ( Socket socket = new ServerSocket(port).accept() ) {


            Thread receiver = new Thread ( ()-> {
                char[] buffer = new char[1024];

                try (BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                     PrintWriter pw = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()), true)) {
                    while (true) {
                        String msg = br.readLine();
                        if (msg != null) {
                            pw.println(msg);
                        } else {
                            System.out.println("Connection closed");
                            break;
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
            receiver.start();

            receiver.join();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }



    public static void main(String[] args) throws Exception {
        Server server = new Server(8000);
        server.init();
    }
}
