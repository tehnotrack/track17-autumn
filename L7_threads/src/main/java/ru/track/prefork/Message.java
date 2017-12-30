package ru.track.prefork;

import java.io.Serializable;

public class Message implements Serializable {
    private long ts;
    private String data;
    private String senderName;

    public Message(long ts, String data, String senderName) {
        this.ts = ts;
        this.data = data;
        this.senderName = senderName;
    }

    public String getData(){
        return data;
    }

    public long getTs(){
        return ts;
    }

    public String getSenderName() { return senderName; }
}

