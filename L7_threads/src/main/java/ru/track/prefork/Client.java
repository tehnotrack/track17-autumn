package ru.track.prefork;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.track.prefork.protocol.*;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Scanner;

public class Client {
    static Logger log = LoggerFactory.getLogger(Client.class);
    private int port;
    private String host;
    private Protocol<Message> protocol;

    public Client(int port, String host, Protocol<Message> protocol) {
        this.port = port;
        this.host = host;
        this.protocol = protocol;
    }

    public static void main(String[] args) {
        Client client = new Client(9000, "localhost", new JsonProtocol());
        try {
            client.loop();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loop() throws Exception {
        System.out.println("Client started!");
        Socket socket = new Socket(host, port);

        try (InputStream in = socket.getInputStream();
             OutputStream out = socket.getOutputStream();) {

            Scanner scanner = new Scanner(System.in);
            Thread scannerThread = new Thread(() -> {
                try {
                    while (true) {
                        // read line
                        String line = scanner.next();

                        Message msg = new Message(System.currentTimeMillis(), line);
                        msg.username = "User";

                        // write to socket
                        out.write(protocol.encode(msg));
                        out.flush();
                        // condition to close socket
                        if (line.equalsIgnoreCase("exit")) {
                            break;
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (ProtocolException e) {
                    e.printStackTrace();
                }
            });
            scannerThread.start();

            byte[] buffer = new byte[1024];
            while (!socket.isOutputShutdown()) {
                // read msg from server
//                int nbytes = in.read(buffer);
//                if (nbytes != -1) {
                    // print msg from server
                    Message msgFromServer = protocol.decode(in, Message.class);
//                    log.info("From server: " + msgFromServer);
//                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            socket.close();
        }
    }
}
