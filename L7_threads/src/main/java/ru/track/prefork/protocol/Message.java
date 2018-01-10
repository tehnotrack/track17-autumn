package ru.track.prefork.protocol;

import java.io.Serializable;

/**
 *
 */
public class Message implements Serializable {
    public long ts;
    public String text;
    public String username;

    public Message(long ts, String text) {
        this.ts = ts;
        this.text = text;
    }

    @Override
    public String toString() {
        return "Message{" +
                "ts=" + ts +
                ", text='" + text + '\'' +
                ", username='" + username + '\'' +
                '}';
    }
}
