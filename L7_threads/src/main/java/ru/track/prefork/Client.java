package ru.track.prefork;

import java.io.*;
import java.net.ProtocolException;
import java.net.Socket;
import java.util.Scanner;
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

        Socket socket  = new Socket(host, port);

        final InputStream in = socket.getInputStream();
        final OutputStream out = socket.getOutputStream();
        Scanner scanner = new Scanner(System.in);

        Thread scannerThread = new Thread(() -> {
            try {
                while (true) {
                    String line = scanner.nextLine();
                   if(line.equals("exit"))
                    {
                        socket.close();
                        System.exit(0);
                       // break;
                    }
                    else{
                        Message msg = new Message(System.currentTimeMillis(), line);
                        out.write(protocol.encode(msg));
                        out.flush();
                }}
            }catch (IOException e){
                e.printStackTrace();
            }

        });
        scannerThread.start();

        byte[] buff = new byte[1024];

        while (true) {

            int nRead = in.read(buff);
            if (nRead != -1) {
                protocol.decode(buff);
            } else {
                log.error("Connection failed");
                return;
            }
        }
        }


    public static void main(String[] args) throws Exception{
        Client client = new Client(9000,"localhost",new BinaryProtocol<>());
        client.loop();
    }

}
