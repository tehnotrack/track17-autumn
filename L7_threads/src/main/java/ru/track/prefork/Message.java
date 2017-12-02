package ru.track.prefork;

import java.io.Serializable;

public class Message implements Serializable {
    private long ts;
    private String data;

    public Message(long ts, String data) {
        this.data = data;
        this.ts = ts;
    }
}
