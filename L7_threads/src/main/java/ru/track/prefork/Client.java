package ru.track.prefork;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.Arrays;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.track.prefork.protocol.JsonProtocol;
import ru.track.prefork.protocol.Message;
import ru.track.prefork.protocol.Protocol;
import ru.track.prefork.protocol.ProtocolException;

public class Client {

    private Logger log = LoggerFactory.getLogger(Client.class);
    private int port;
    private String host;
    private Protocol<Message> protocol;

    public Client(int port, String host, Protocol<Message> protocol) {
        this.port = port;
        this.host = host;
        this.protocol = protocol;
    }

    public static void main(String[] args) throws Exception {
        Client client = new Client(9000, "localhost", new JsonProtocol());
        client.loop();
    }

    private void loop() throws IOException {
        int count = 0;
        final int maxTries = 10;
        int timeout = 1;
        Socket tryToConnectSocket;
        while (true) {
            try {
                tryToConnectSocket = new Socket(host, port);
                break;
            } catch (Exception e) {
                if (++count == maxTries) {
                    log.error("Can't connect, exiting ");
                    return;
                }
                log.info(String.format("Can't connect, next try in %d sec ", timeout));
                try {
                    TimeUnit.SECONDS.sleep(timeout);
                    timeout *= 2;
                } catch (InterruptedException e1) {
                    e1.printStackTrace();
                }
            }
        }
        Socket socket = tryToConnectSocket;
        log.info(String.format("Connected to %s:%d", host, port));

        try (InputStream in = socket.getInputStream();
            OutputStream out = socket.getOutputStream();) {

            Scanner scanner = new Scanner(System.in);
            Thread scannerThread = new Thread(() -> {
                try {
                    while (!Thread.currentThread().isInterrupted()) {
                        if (scanner.hasNextLine()) {
                            String line = scanner.nextLine();
                            Message msg = new Message(System.currentTimeMillis(), line);
                            msg.username = "User";
                            out.write(protocol.encode(msg));
                            out.flush();
                            if (line.equalsIgnoreCase("exit")) {
                                Thread.currentThread().interrupt();
                                break;
                            }
                        }
                    }
                } catch (IOException e) {
                    log.error("IO exception", e);
                } catch (ProtocolException e) {
                    log.error("Protocol exception", e);
                }
            });
            scannerThread.setDaemon(true);
            scannerThread.start();

            byte[] buffer = new byte[1024];
            try {
                while (!scannerThread.isInterrupted()) {
                    int nbytes = in.read(buffer);
                    if (nbytes != -1) {
                        Message msgFromServer = protocol
                            .decode(Arrays.copyOfRange(buffer, 0, nbytes),
                                Message.class);
                        log.info("decode: " + msgFromServer);
                    } else {
                        scannerThread.interrupt();
                        break;
                    }
                }
            } finally {
                scannerThread.interrupt();
            }
        } catch (SocketTimeoutException e) {

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
