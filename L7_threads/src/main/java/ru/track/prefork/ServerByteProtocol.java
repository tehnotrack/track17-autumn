package ru.track.prefork;

import java.net.Socket;

public abstract class ServerByteProtocol {

    private Socket socket;

    public ServerByteProtocol(Socket socket){
        this.socket = socket;
    }

    abstract public byte[] read() throws ServerByteProtocolException;

    abstract public void write(byte data[]) throws ServerByteProtocolException;

    public Socket getSocket() {
        return socket;
    }
}
