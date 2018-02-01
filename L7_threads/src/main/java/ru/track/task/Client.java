package ru.track.task;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.track.task.protocol.JavaSerializationProtocol;
import ru.track.task.protocol.Message;
import ru.track.task.protocol.Protocol;
import ru.track.task.protocol.ProtocolException;

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
    private boolean exited = false;

    public Client(int port, String host, Protocol<Message> protocol) {
        this.port = port;
        this.host = host;
        this.protocol = protocol;
    }

    public void exit() {
        exited = true;
        log.info("exiting");
    }

    public void loop() throws Exception {
        Socket socket = new Socket(host, port);

        final OutputStream out = socket.getOutputStream();
        final InputStream in = socket.getInputStream();

        Scanner scanner = new Scanner(System.in);

        Thread scannerThread = new Thread(new Runnable() {
            private boolean exiting = false;

            @Override
            public void run() {
                try {
                    while (!this.exiting) {
                        String line = scanner.nextLine();
                        Message msg = new Message(System.currentTimeMillis(), line);

                        if (msg.text.equals(Server.exitCommand)) {
                            log.info("exiting process initiated");
                            this.exiting = true;
                        }

                        msg.username = "anon";
                        out.write(protocol.encode(msg));
                        out.flush();
                    }
                    log.info("scanner thread terminated");
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (ProtocolException e) {
                    e.printStackTrace();
                }
            }
        });

        scannerThread.start();

        byte[] buf = new byte[1024];
        while (!exited) {
            int nRead = in.read(buf);
            if (nRead != -1) {
                Message fromServer = protocol.decode(buf);
                if (fromServer.text.equals(Server.exitConfirmation)) {
                    socket.close();
                    exit();
                    return;
                }
                System.out.println(fromServer.username + ": " + fromServer.text);
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
