package ru.track.prefork;

import java.io.*;
import java.net.Socket;
import java.util.List;

public class AloneThread implements Runnable {
    private Socket socket;
    private InputStream in;
    private int message;
    private byte[] msg = new byte[1024];
    private String str;
    private String name;
    private User user;
    private List<User> users;

    public AloneThread(User user, List<User> users) throws IOException {
        this.user = user;
        this.socket = user.getSocket();
        this.in = socket.getInputStream();
        this.users = users;
        this.name = user.getName();
        sendMessage(name + "connected to chat");
    }

    public void run() {
        try {
            while (!socket.isClosed()) {
                message = in.read(msg);
                str = new String(msg, 0, message);
                System.out.println("Get from client "  + str);
                if (str == null || str.equals("exit")) {
                    break;
                }
                sendMessage(name + ">" + str);
            }
        } catch (IOException e) {

        } finally {
            try {
                System.out.println("closing conection : " + socket);
                sendMessage(name + "left chat");
                socket.close();
                users.remove(user);
            } catch (IOException e) {
                System.err.println("Socket not closed");
            }
        }
    }

    public void sendMessage(String str) throws IOException {
        for (User u : users) {
            if (!u.getName().equals(name)) {
                System.out.println("Sending to client " + u.getName() + " " + str);
                u.getSocket().getOutputStream().write((name + ">" + str).getBytes());
            }
        }
    }
}