package ru.track.prefork;

import java.io.*;
import java.net.ProtocolException;
import java.net.Socket;
import java.util.Scanner;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 */
public class Client {
    private int port;
    private String host;
    static Logger log = LoggerFactory.getLogger(Client.class);
    private Protocol<Message> protocol;

    public Client(int port, String host, Protocol<Message> protocol) {
        this.port = port;
        this.host = host;
        this.protocol = protocol;
    }

    public void loop() throws IOException {

        final Socket socket = new Socket(host, port);

        try {

            final InputStream in = socket.getInputStream();
            final OutputStream out = socket.getOutputStream();
            Scanner scanner = new Scanner(System.in);

            Thread scannerThread = new Thread(() -> {
                try {
                    while (true) {

                        String line = scanner.nextLine();
                        if(line.equals("exit"))
                        {
                            break;
                        }
                        Message msg = new Message(System.currentTimeMillis(), line);
                        out.write(protocol.encode(msg));
                        out.flush();


                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }
                finally{
                    Thread.currentThread().interrupt();
                    IOUtils.closeQuietly(socket);
                }



            });
            scannerThread.setDaemon(true);
            scannerThread.start();


            byte[] buff = new byte[1024];

            while (!socket.isClosed() && !Thread.interrupted()) {

                int nRead = in.read(buff);
                if (nRead != -1) {

                    Message msg = protocol.decode(buff);

                    System.out.println(msg.text);


                } else {
                    log.error("Connection failed");
                    return;
                }
            }

        } catch (IOException e) {

            e.printStackTrace();
        } finally {

            IOUtils.closeQuietly(socket);
        }
    }


    public static void main(String[] args) throws Exception {
        Client client = new Client(9000, "localhost", new BinaryProtocol<>());
        try {
            client.loop();
        } catch (IOException e) {

            e.printStackTrace();
        }
    }

}
