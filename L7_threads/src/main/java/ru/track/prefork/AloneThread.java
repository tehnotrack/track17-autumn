package ru.track.prefork;

import java.io.*;
import java.net.Socket;
import java.net.SocketException;
import java.util.concurrent.ConcurrentMap;

public class AloneThread implements Runnable {
    private Socket socket;
    private InputStream in;
    private int message;
    private byte[] msg = new byte[1024];
    private String str;
    private String name;
    private User user;
    private ConcurrentMap<Long, User>  users;
    BinaryProtocol<Message> protocol = new BinaryProtocol<>();

    public AloneThread(User user, ConcurrentMap<Long, User> users) throws IOException {
        this.user = user;
        this.socket = user.getSocket();
        this.in = socket.getInputStream();
        this.users = users;
        this.name = user.getName();
        sendMessage(" connected to chat");
    }

    public void run() {
        try {
            while (!socket.isClosed()) {
                try {
                    message = in.read(msg);
                    if (message != -1) {
                        Message message1 = (Message) protocol.decode(msg);
                        str = message1.getData();
                        if (str == null || str.equals("exit")) {
                            break;
                        }
                        sendMessage(">" + str);
                    } else {
                        System.err.println("User was disconected");
                        break;
                    }
                } catch (SocketException e) {
                    System.err.println("closed connection to Client["
                            + user.getId() + "]");
                }
            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }  finally {
            try {
                System.out.println("closing conection : " + socket);
                sendMessage( " left chat");
                socket.close();
                users.values().remove(user);
            } catch (IOException e) {
                System.err.println("Socket not closed");
            }
        }
    }

    public void sendMessage(String str) throws IOException {
        Message message = new Message(name + ">" + str);
        for (User u : users.values()) {
            if (!u.equals(user)) {
                u.getSocket().getOutputStream()
                        .write(protocol.encode(message));
                u.getSocket().getOutputStream().flush();
            }
        }
    }
}