package ru.track.prefork;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;

import static java.lang.System.exit;

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
            exit(-1);
        }

        try (InputStream in = socket.getInputStream(); OutputStream out = socket.getOutputStream())
        {
            Thread threadToWrite = new Thread(() -> {
                try {
                    while (!Thread.interrupted()) {
                        String toServer = fromKeyboard.readLine();
                        if (!toServer.isEmpty()) {
                            Message message = new Message(toServer);
                            out.write(protocol.encode(message));
                            out.flush();
                        }
                    }
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            });
            threadToWrite.setDaemon(true);
            threadToWrite.start();

            byte[] buffer = new byte[1024];
            try {
                while (true) {
                    int nRead = in.read(buffer);
                    Message fromServer = protocol.decode(buffer);
                    if (nRead == -1 || fromServer.text.equalsIgnoreCase("exit")) {
                        log.info("you quited chat room");
                        threadToWrite.interrupt();
                        break;
                    } else System.out.println(fromServer.toString());
                }
            } catch (IOException ex) {
                log.error("Server has died");
            }

        }
        catch (IOException ex) {
            log.error("check input/output streams");
        }
        finally {
            IOUtils.closeQuietly(socket);
        }
    }

    public static void main (String[] args) throws Exception {
        Client client = new Client(9000, "localhost", new BinaryProtocol<>());
        client.loop();
    }
}