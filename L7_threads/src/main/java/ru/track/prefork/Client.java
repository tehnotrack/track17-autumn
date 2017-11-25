package ru.track.prefork;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;

/**
 *
 */
public class Client extends Thread{
    private int port;
    private String host;

    public Client(int port, String host) {
        this.port = port;
        this.host = host;

    }

    public void run(){
        try {
            Socket cs = new Socket(host, port);
            PrintWriter pw = new PrintWriter(new OutputStreamWriter(cs.getOutputStream()), true);
            Scanner in=new Scanner(System.in);
            String s = in.nextLine();
            pw.println(s);
            BufferedReader br = new BufferedReader(new InputStreamReader(cs.getInputStream()));
            String line = br.readLine();
            System.out.println(line);
            cs.close();
        }catch ( IOException e){
            e.printStackTrace();
        }
    }
    public static void  main(String[] args){
        Client client=new Client(8080, "localhost");
        client.run();
    }
}
