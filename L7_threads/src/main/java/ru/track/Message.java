package ru.track;

import java.io.Serializable;

public class Message implements Serializable{
    long ts;
    String data;
    boolean connected = true;

    public Message(long ts, String data) {

        this.ts = ts;
        this.data = data;
    }

    public Message(long ts, String data, boolean connected) {

        this.ts = ts;
        this.data = data;
        this.connected = connected;
    }
}
