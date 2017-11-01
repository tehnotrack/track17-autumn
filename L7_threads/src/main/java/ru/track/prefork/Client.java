package ru.track.prefork;

import java.io.*;
import java.net.Socket;

import static java.lang.Thread.sleep;

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

    public static void main(String[] args) throws IOException {
        Socket socket = new Socket("localhost", 8000);
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        DataOutputStream oos = new DataOutputStream(socket.getOutputStream());
        DataInputStream ois = new DataInputStream(socket.getInputStream());


        while (!socket.isOutputShutdown()) {
            try {
                if (br.ready()) {

                    System.out.println("Client start writing in channel...");
                    Thread.sleep(3000);
                    String clientCommand = br.readLine();
                    oos.writeUTF(clientCommand);
                    oos.flush();
                    System.out.println("Clien sent message " + clientCommand + " to server.");
                    Thread.sleep(1000);

                    if (clientCommand.equalsIgnoreCase("exit")) {
                        System.out.println("Client kill connections");
                        Thread.sleep(1000);
                        System.out.println(ois.readUTF());
                        if (ois.read() > -1) {
                            System.out.println("reading...");
                            String in = ois.readUTF();
                            System.out.println(in);
                            break;
                        }
                    }

                }

                //sleep(3000);

                //System.out.println("w8ing for server answer");


// если успел забираем ответ из канала сервера в сокете и сохраняем её в ois переменную,  печатаем на свою клиентскую консоль




        } catch(Exception e){
            e.printStackTrace();
        }

            //System.out.println("azazazaz");
//            if (ois.read() > -1) {
//                System.out.println("reading...");
//                String in = ois.readUTF();
//                System.out.println(in);
//            }
        }

    }
}
