package ru.track.prefork;

import java.net.Socket;

public class User {
    private String name;
    private Socket socket;
    private Long id;
    User () {

    }

    User (String name, Socket socket, Long id){
        this.name = name;
        this.socket = socket;
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public Long getId() {
        return id;
    }

    public Socket getSocket() {
        return socket;
    }
}
