package ru.track.prefork;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Scanner;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ru.track.prefork.protocol.JavaSerializationProtocol;
import ru.track.prefork.protocol.Message;
import ru.track.prefork.protocol.Protocol;
import ru.track.prefork.protocol.ProtocolException;

/**
 *
 */
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

    public void loop() throws Exception {
        Socket socket = new Socket(host, port);

        final OutputStream out = socket.getOutputStream();
        final InputStream in = socket.getInputStream();

        Scanner scanner = new Scanner(System.in);
        Thread scannerThread = new Thread(() -> {
            try {
                while (true) {
                    String line = scanner.nextLine();
                    Message msg = new Message(System.currentTimeMillis(), line);
                    msg.username = "Dima";
                    out.write(protocol.encode(msg));
                    out.flush();
                }
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ProtocolException e) {
                e.printStackTrace();
            }
        });
        scannerThread.start();

        byte[] buf = new byte[1024];
        while (true) {
            int nRead = in.read(buf);
            if (nRead != -1) {
                protocol.decode(buf);
            } else {
                log.error("Connection failed");
                return;
            }
        }
    }

    public static void main(String[] args) throws Exception {
        Client client = new Client(9000, "localhost", new JavaSerializationProtocol());
        client.loop();
    }
}
