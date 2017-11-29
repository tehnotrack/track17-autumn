package ru.track.prefork.protocol;

import java.io.Serializable;


public class Message implements Serializable {

    public long ts;
    public String data;
    public String username;

    public Message(long ts, String data) {
        this.ts = ts;
        this.data = data;

    }

    @Override
    public String toString() {
        return "Message{" +
                "ts" + ts +
                ", message_text='" + data + '\'' +
                ", username='" + username + '\'' +
                "}";

    }
}