package ru.track.prefork;

import com.sun.xml.internal.ws.policy.privateutil.PolicyUtils;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

/**
 *
 */
public class Server {
    private int port;
    ServerSocket ss;
    boolean needConnection;
    public Server(int port) {
        needConnection = true;
        this.port = port;
    }
    class HttpConnect extends Thread{
        private Socket sock;
        HttpConnect(Socket s){
            sock=s;
            setPriority(NORM_PRIORITY-1);
            start();

        }
        public void run(){
            try {
                PrintWriter pw = new PrintWriter(new OutputStreamWriter(sock.getOutputStream()), true);
                BufferedReader br = new BufferedReader(new InputStreamReader(sock.getInputStream()));
                String req = br.readLine();
                System.out.println("Request: "+req);
                pw.println(req);
                //pw.flush();
            }catch (IOException e){
                e.printStackTrace();
            }
        }
    }
    public void runServer(){
        try {
            ss = new ServerSocket(port);
            Scanner in = new Scanner(System.in);
            while (needConnection){
                new HttpConnect(ss.accept());
                if(in.nextLine()=="exit"){
                    needConnection = false;
                }
            }
            ss.close();
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    public static void main(String[] args){
        Server server = new Server(8080);
        server.runServer();
    }

}
