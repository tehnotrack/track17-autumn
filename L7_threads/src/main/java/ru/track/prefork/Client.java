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
import org.apache.commons.io.IOUtils;
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
        int connect_timeout = 200;
        int retry_timeout = 2;
        int request_timeout = 200;
        Socket tryToConnectSocket;
        while (true) {
            try {
                tryToConnectSocket = new Socket();
                tryToConnectSocket.connect(new InetSocketAddress(host, port), connect_timeout);
                tryToConnectSocket.setSoTimeout(request_timeout);
                break;
            } catch (Exception e) {
                if (++count == maxTries) {
                    log.error("Can't connect, exiting ");
                    return;
                }
                log.info(String.format("Can't connect, try again in %d sec ", retry_timeout));
                try {
                    TimeUnit.SECONDS.sleep(retry_timeout);
                    retry_timeout *= 2;
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
                    int nbytes = 0;
                    try {
                        nbytes = in.read(buffer);
                    } catch (SocketTimeoutException ste) {
                        TimeUnit.MILLISECONDS.sleep(500);
                    }
                    if (nbytes == 0) {
                        continue;
                    } else if (nbytes != -1) {
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
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            IOUtils.closeQuietly(socket);
            log.info("Connection dropped, exiting...");
        }
    }
}
