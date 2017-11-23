package ru.track.prefork;

import com.sun.imageio.spi.OutputStreamImageOutputStreamSpi;
import ru.track.prefork.protocol.JavaSerializationProtocol;
import ru.track.prefork.protocol.Message;
import ru.track.prefork.protocol.Protocol;
import ru.track.prefork.protocol.ProtocolException;

import java.awt.image.ImagingOpException;
import java.io.*;
import java.net.Socket;
import java.util.Arrays;
import java.util.Scanner;

/**
 *
 */
public class Client {
    private int port;
    private String host;
    private Socket clientSocket;
    private Protocol<Message> protocol;

    private Client(int port, String host, Protocol<Message> p) throws IOException {
        this.port = port;
        this.host = host;
        protocol = p;
    }


    void connect() throws IOException {
        clientSocket = new Socket(host, port);
        Scanner in = new Scanner(System.in);
        InputStream reader = clientSocket.getInputStream();
        OutputStream writer = clientSocket.getOutputStream();

        new Thread(() -> {
            try {
                while (true) {
                    String currStr = in.nextLine();
                    if (currStr.equals("exit")) {
                        break;
                    }
                    Message msg = new Message(System.currentTimeMillis(), currStr, "Irina");
                    writer.write(protocol.encode(msg));
                    writer.flush();
                }
            } catch (IOException exc) {
                exc.printStackTrace();
                throw new RuntimeException(exc);
            } catch (ProtocolException exc) {
                exc.printStackTrace();
                throw new RuntimeException(exc);
            }
        }).start();

        byte[] buf = new byte[1024];
        while (true) {
            int nRead = reader.read(buf);
            if (nRead != -1) {
                try {
                    protocol.decode(buf);
                } catch (ProtocolException exc) {
                    exc.printStackTrace();
                }
            } else {
                break;
            }
        }

        clientSocket.close();
    }

    public static void main(String[] args) {
        try {
            Client c = new Client(8080, "localhost", new JavaSerializationProtocol());
            c.connect();
        }
        catch (IOException exc) {
            System.out.println(exc.getMessage());
        }
    }
}
