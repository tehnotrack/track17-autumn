package ru.track.prefork;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.concurrent.ConcurrentMap;


public class Adminka implements Runnable {
    ConcurrentMap<Long, User> users;
    BinaryProtocol<Message> protocol = new BinaryProtocol<>();
    Adminka (ConcurrentMap<Long, User> users) {
        this.users = users;
    }
    @Override
    public void run() {
        try (BufferedReader br = new BufferedReader(
                     new InputStreamReader(System.in))){
            String str;
            Message message;
            while (true) {
                str = br.readLine().toLowerCase();
                if (str.equals("list")) {
                    list();
                } else if (str.startsWith("drop ")) {
                    try {
                        Long id = Long.parseLong(str.substring(5));
                        if (users.get(id) != null) {
                                kick(id);
                        } else {
                            System.err.println("Client does not exist");
                        }
                    } catch (Exception e) {
                        System.err.println("wrong format");
                    }
                }
            }
        } catch (IOException e) {}
    }

    public void list () {
        for (User u : users.values()) {
            System.out.println(u.getName());
        }
    }
    public void kick (Long id) {
        Message message = new Message("exit");
        try {
                users.get(id).getSocket().getOutputStream()
                        .write(protocol.encode(message));
                users.get(id).getSocket().close();
                users.remove(id);

        } catch (IOException e) {
            System.err.println("input mistake");
        }
    }
}
