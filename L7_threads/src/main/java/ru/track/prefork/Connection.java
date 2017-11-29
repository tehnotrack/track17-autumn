package ru.track.prefork;

import java.net.Socket;

public class Connection {
    private int id;
    private String host;
    private int port;
    private Socket socket;
    private String clientInfo;

    public Connection(int id, String host, int port, Socket socket) {
        this.id = id;
        this.host = host;
        this.port = port;
        this.socket = socket;
        this.clientInfo = "Client[" + this.id + "]@" + this.host + ":" + this.port;
    }

    public String getClientInfo() {
        return clientInfo;
    }

    public Socket getSocket() {
        return socket;
    }

    public int getId() {
        return id;
    }
}
