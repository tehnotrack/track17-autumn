package ru.track.prefork;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.ws.ProtocolException;
import java.io.*;
import java.net.Socket;

/**
 *
 */
public class Client {
    private int port;
    private String host;
    private Socket socket;
    private Protocol<MyMessage> protocol;
    private Logger log;


    public Client(int port, String host, Protocol<MyMessage> protocol) {
        this.log = LoggerFactory.getLogger(Client.class);
        log.info("Init client socket");
        this.port = port;
        this.host = host;
        this.protocol = protocol;
        if (runClient() == 0) {
            clientHandler();
        }
    }

    private int runClient() {
        try {
            log.info("Try to connect to server");
            socket = new Socket(this.host, this.port);
            log.info("Successfully connected");
        } catch (IOException e) {
            log.error("Error while connection: " + e.getLocalizedMessage());
            return (-1);
        }
        return 0;
    }

    private void clientHandler() {
        byte[] buf = new byte[1024];

        try (
                BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
                OutputStream out = socket.getOutputStream();
                InputStream in = socket.getInputStream()
        ) {

            log.info("Start reading thread");

            Thread readerThread = new Thread(() -> {
                try {
                    while (!socket.isOutputShutdown()) {
                        if (br.ready()) {
                            String data = br.readLine();
                            log.info("User write: " + data);
                            MyMessage msg = new MyMessage(System.currentTimeMillis() / 1000L, data);
                            out.write(protocol.encode(msg));
                            if (data.equalsIgnoreCase("exit")) {
                                log.info("Exit...");
                                break;
                            }
                        }
                    }
                } catch (IOException e) {
                    log.error("Reader error: " + e.getLocalizedMessage());
                }

            });

            readerThread.start();

            while (!socket.isClosed() && !Thread.interrupted()) {
                try {
                    log.info("Try to read");
                    int nRead = in.read(buf);
                    if (nRead != -1) {
                        MyMessage data = protocol.decode(buf);
                        log.info("Response read");
                        log.info("I have read: " + data.text);
                        System.out.println(data.text);
                    } else {
                        break;
                    }
                } catch (IOException e) {
                    log.error("Error while reading: " + e.getLocalizedMessage());
                    break;
                } catch (ProtocolException e) {
                    log.error("Error while decoding: " + e.getLocalizedMessage());
                }
            }

            System.out.println("Try write something to server:");


        } catch (IOException e) {
            log.error("Error: " + e.getLocalizedMessage());
        }
    }

    public static void main(String[] args) throws Throwable {
        Client client = new Client(8080, "localhost", new BinaryProtocol<>());
    }
}
