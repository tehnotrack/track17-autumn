package ru.track.prefork;

import org.jetbrains.annotations.Nullable;
import ru.track.prefork.exceptions.NoThreadSpecified;

import java.io.IOException;
import java.net.Socket;

public class ServerConnection {
    private int id;
    private String host;
    private int port;
    private Socket socket;
    private String clientInfo;
    private Thread thread = null;

    public ServerConnection(int id, String host, int port, Socket socket) {
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

    @Nullable
    public Thread getThread() {
        return thread;
    }

    public void setThread(Thread thread) {
        this.thread = thread;
    }

    public void drop() throws IOException, NoThreadSpecified {
        if (thread != null) {
            thread.interrupt();
        } else {
            throw new NoThreadSpecified("Thread is null");
        }

        socket.close();
    }
}
