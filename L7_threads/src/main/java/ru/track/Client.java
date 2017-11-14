package ru.track;

import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class Client {
    private String host;
    private int port;
    private final Socket socket;
    private Thread receiver;
    private Thread sender;
    private static Logger logger = LoggerFactory.getLogger("Client");

    public Client(@NotNull String host, int port) throws IOException {
        this.host = host;
        this.port = port;
        socket = new Socket(host, port);

    }

    private void shutdown() throws IOException {
        if (socket != null)
            socket.close();
    }

    public static void main(String[] args) throws IOException {
        Client client = new Client(args[0], Integer.parseInt(args[1]));

        client.sender = new Thread() {
            @Override
            public void run() {
                Scanner sc = new Scanner(System.in);
                String input;
                try(ObjectOutputStream oos = new ObjectOutputStream(client.socket.getOutputStream())) {

                    while (!currentThread().isInterrupted()) {
                        input = sc.nextLine();
                        if(isInterrupted()){
                            return;
                        }

                        oos.writeObject(new Message(System.currentTimeMillis(), input));
                        oos.flush();
                        if (input.equals("exit")) {
                            client.receiver.interrupt();
                            client.shutdown();
                            break;
                        }
                    }
                } catch (IOException e) {
                    if(!isInterrupted()){
                        logger.error("Troubles in connection: {}", e);
                    }
                }
            }

        };

        client.receiver = new Thread() {
            @Override
            public void run() {
                try(ObjectInputStream ios = new ObjectInputStream(client.socket.getInputStream())) {
                    Message msg;
                    while (!currentThread().isInterrupted()) {
                        msg = (Message) ios.readObject();
                        if(!msg.connected){
                            logger.info("Dropped from server");
                            client.sender.interrupt();
                            return;
                        }
                        logger.info( msg.data);
                    }
                } catch (IOException e) {
                    if(!isInterrupted()){
                        logger.error("Troubles in connection: {}", e);
                    }
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }
        };

        client.sender.start();
        client.receiver.start();
    }
}
