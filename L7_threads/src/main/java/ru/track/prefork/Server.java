package ru.track.prefork;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import static java.lang.Thread.sleep;

/**
 *
 */
public class Server {
    private int port;

    public Server(int port) {
        this.port = port;
    }

    public static void main(String[] args) throws IOException {
        String entry;
        while(true) {
            try {
                ServerSocket server = new ServerSocket(8000);
                System.out.println("Server started");
                Socket client = server.accept();

                DataOutputStream out = new DataOutputStream(client.getOutputStream());
                System.out.println("DataOutputStream  created");

                DataInputStream in = new DataInputStream(client.getInputStream());
                System.out.println("DataInputStream created");


                while (!client.isClosed()) {

                    System.out.println("Server reading from channel");

                    entry = in.readUTF();

// после получения данных считывает их
                    System.out.println("READ from client message - " + entry);

// и выводит в консоль
                    System.out.println("Server try writing to channel");
                    //System.out.println(entry);
// инициализация проверки условия продолжения работы с клиентом по этому сокету по кодовому слову       - quit
                    if (entry.equalsIgnoreCase("exit")) {
                        System.out.println("Client initialize connections suicide ...");
                        out.writeUTF("Server reply - " + entry + " - OK");
                        out.flush();
//                        try {
//                            //sleep(2000);
//                        } catch (InterruptedException e) {
//                            e.printStackTrace();
//                        }
                        break;
                    }

                    //System.out.println(entry);
                    out.writeUTF(entry);
                    System.out.println("Server Wrote message to client.");
                    System.out.println(entry);
// освобождаем буфер сетевых сообщений (по умолчанию сообщение не сразу отправляется в сеть, а сначала накапливается в специальном буфере сообщений, размер которого определяется конкретными настройками в системе, а метод  - flush() отправляет сообщение не дожидаясь наполнения буфера согласно настройкам системы
                    out.flush();
                }

// если условие выхода - верно выключаем соединения
                System.out.println("Client disconnected");
                System.out.println("Closing connections & channels.");

                // закрываем сначала каналы сокета !
                in.close();
                out.close();

                // потом закрываем сам сокет общения на стороне сервера!
                //client.close();
            } catch (Exception e) {

            }
        }
    }
}
