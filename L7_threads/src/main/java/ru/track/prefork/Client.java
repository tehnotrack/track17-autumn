package ru.track.prefork;

import java.io.*;
import java.net.Socket;

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

    public void createConnection() {
        try (Socket socket = new Socket("localhost", 5000);
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             BufferedReader stdin = new BufferedReader(new InputStreamReader(System.in));
        ) {
            String  outstr;
            while (!socket.isClosed() && ((outstr = stdin.readLine()) != null)) {
                //чтение
                if (outstr.equalsIgnoreCase("exit")) {
                    break;
                }
                else //произвольная строка
                {
                    out.println(outstr);
                }
                //запись
                String s = in.readLine();
                if (!s.isEmpty()) {
                    System.out.println(s);
                }
            }
        }
        catch(java.io.IOException e){
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        Client client = new Client(5000, "localhost");
        client.createConnection();
    }

}
