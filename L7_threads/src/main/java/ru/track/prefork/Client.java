package ru.track.prefork;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;

public class Client {
    private int port;
    private String host;
    private Protocol<Message> protocol;
    Logger log = LoggerFactory.getLogger(Server.class);

    public Client(int port, String host, Protocol<Message> protocol) {
        this.port = port;
        this.host = host;
        this.protocol = protocol;
    }

    public void loop() throws Exception {
        Socket socket = null;
        BufferedReader fromKeyboard = new BufferedReader(new InputStreamReader(System.in));

        try {
            socket = new Socket(host, port);
        } catch (UnknownHostException uhe) {
            log.error ("cant open socket");
            System.exit(-1);
        }

        InputStream in = socket.getInputStream();
        OutputStream out = socket.getOutputStream();

        Thread threadToWrite = new Thread(() -> {
            try {
                while (true) {
                    String toServer = fromKeyboard.readLine();
                    Message message = new Message(toServer);
                    out.write(protocol.encode(message));
                    out.flush();
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        });
        threadToWrite.start();


        byte [] buffer = new byte [1024];
        while (true){
            int nRead = in.read(buffer);
            Message fromServer = protocol.decode(buffer);
            if (nRead == -1 || fromServer.equals("exit")) {
                out.close();
                fromKeyboard.close();
                in.close();
                return;
            }
            else System.out.println(fromServer.toString());
        }
    }

    public static void main (String[] args) throws Exception {
        Client client = new Client(9000, "localhost", new BinaryProtocol<>());
        client.loop();
    }
}