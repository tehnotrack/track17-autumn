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

    private void listen(ObjectOutputStream os, ObjectInputStream is) {
        try {
            Message msg;
            while (true) {
                try {
                    msg = (Message) is.readObject();
                    System.out.println(msg.getData());
                }
                catch (ClassNotFoundException e){log.error("Message decoding failed");}
            }
        }
        catch(IOException e) {}
        finally {
            IOUtils.closeQuietly(os);
            IOUtils.closeQuietly(is);
        }
    }

    private void send(ObjectOutputStream os, ObjectInputStream is) {
        try (Scanner in = new Scanner(System.in)){
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
        }
        catch(IOException e) {}
        finally{
            IOUtils.closeQuietly(os);
            IOUtils.closeQuietly(is);
        }
    }

    public void start() throws IOException {
        Socket sock = null;
        Thread t;
        try {
            sock = new Socket(host, port);
            final ObjectOutputStream os = new ObjectOutputStream(sock.getOutputStream());
            final ObjectInputStream is = new ObjectInputStream(sock.getInputStream());
            t = new Thread(() -> {send(os, is);});
            t.setDaemon(true);
            t.start();
            listen(os, is);
        }
        finally {
            IOUtils.closeQuietly(sock);
            log.info("Disconnected from server");
        }
    }
    public static void main(String[] args) throws Exception {
        final Client client = new Client(8100,"localhost");
        client.start();
    }
}
