package ru.track.prefork;

import java.net.Socket;

public class User {
    private String name;
    private Socket socket;
    private Long id;

    User () {

    }

    User (String name, Socket socket){
        this.name = name;
        this.socket = socket;
    }

    User (String name, Socket socket, Long id){
        this.name = name;
        this.socket = socket;
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setSocket(Socket socket) {
        this.socket = socket;
    }

    public Long getId() {
        return id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Socket getSocket() {
        return socket;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
