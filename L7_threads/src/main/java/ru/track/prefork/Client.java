package ru.track.prefork;

//import com.sun.tools.jdeprscan.scan.Scan;
//import com.sun.org.apache.xpath.internal.operations.String;
//import com.sun.java.util.jar.pack.Package;
import org.apache.commons.io.IOUtils;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.track.workers.NioClient;

import java.io.*;
import java.net.Socket;
import java.security.Signature;
import java.util.Scanner;

/**
 *
 */
public class Client {
    private static Protocol<Message> protocol = new BinaryProtocol<>();
    private boolean smthWentWrong;
    static Logger log = LoggerFactory.getLogger(Client.class);
    private int port;
    private String host;
    Socket socket;

    private class ReadClient extends Thread {
        InputStream inputStream;
        private ReadClient() {
            inputStream = null;
        }

        @Override
        public void run() {
            readClient();
        }

        private void readClient() {
            try {
                while (true) {
                    inputStream = socket.getInputStream();
                    byte[] buffer = new byte[2048];
                    int nByte = inputStream.read(buffer);
                    if (nByte < 0 || smthWentWrong) {
                        log.info("server is down");
                        smthWentWrong = true;
                        break;
                    }
                    Message message = protocol.decode(buffer);
                    System.out.println(message.toString());
//                    System.out.println(new String(buffer, 0, nByte));
                }
            } catch (IOException e) {
                System.out.println(e.getMessage());
                smthWentWrong = true;
            }
            finally {
                try {
                    inputStream.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public Client(@NotNull String host, int port) {
        this.host = host;
        this.port = port;
        this.socket = null;
        this.smthWentWrong = false;
    }

    private boolean iterrupt(String str) {
        if (str.equals("exit"))
            return true;
        return false;
    }


    public void loop() {


        try {
            socket = new Socket(String.valueOf(host), port);
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
        ReadClient readClient = new ReadClient();
        readClient.start();

        try (
                Scanner scanner = new Scanner(System.in);
                OutputStream out = socket.getOutputStream();
        ) {
            while (true) {
                String line = scanner.nextLine();
                if (iterrupt(line))
                    smthWentWrong = true;
                if (smthWentWrong) {
                    break;
                }
                out.write(line.getBytes());
                out.flush();

            }
        } catch (Exception e) {
            smthWentWrong = true;
            System.out.println(e.getMessage());
        }

    }

    public static void main(String[] args) throws Exception {
        final Client client = new Client("localhost", 9000);
        client.loop();
    }
}
