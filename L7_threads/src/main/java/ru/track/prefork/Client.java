package ru.track.prefork;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;


/**
 *
 */
public class Client {
    static Logger log = LoggerFactory.getLogger(Client.class);
    private int port;
    private String host;

    public Client(int port, String host) {
        this.port = port;
        this.host = host;
    }

    private void logexception(Exception e){
        log.error(e.toString());
        for (StackTraceElement elem : e.getStackTrace())
            log.error("at " + elem.toString());
    }

    private void listen(ObjectInputStream is, Socket sock) {
        try {
            Message msg;
            while (true) {
                try {
                    msg = (Message) is.readObject();
                    System.out.println(msg.getData());
                } catch (ClassNotFoundException e) {
                    log.error("Message decoding failed");
                    logexception(e);
                }
            }
        } catch (IOException e) {
            logexception(e);
        } finally {
            IOUtils.closeQuietly(sock);
        }
    }

    private void send(ObjectOutputStream os, Socket sock) {
        try (Scanner in = new Scanner(System.in)) {
            String str;
            Message msg;
            while (true) {
                str = in.nextLine();
                if (str.equals("exit")) break;
                else if (str.length() > 0) {
                    msg = new Message(System.currentTimeMillis(), str);
                    os.writeObject(msg);
                    os.flush();
                }
            }
        } catch (IOException e) {
            log.error("Message sending failed");
            logexception(e);
        } finally {
            IOUtils.closeQuietly(sock);
        }
    }

    public void start() {
        Socket sock = null;
        Thread t;
        try {
            sock = new Socket(host, port);
            final Socket fsock = sock;
            final ObjectOutputStream os = new ObjectOutputStream(sock.getOutputStream());
            final ObjectInputStream is = new ObjectInputStream(sock.getInputStream());
            t = new Thread(() -> {
                send(os, fsock);
            });
            t.setDaemon(true);
            t.start();
            listen(is, sock);
        } catch (IOException e) {
            log.error("Connection to server failed");
            logexception(e);
        } finally {
            IOUtils.closeQuietly(sock);
            log.info("Disconnected from server");
        }
    }

    public static void main(String[] args) {
        final Client client = new Client(8100, "localhost");
        client.start();
    }
}
