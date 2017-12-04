package ru.track.prefork;

import java.io.Serializable;

public class Message implements Serializable {

    private long time;
    private String author = null;
    private String text;

    public Message(String text) {
        this.text = text;
        time = System.currentTimeMillis();
    }

    public Message(long time, String text) {
        this.text = text;
        this.time = time;
    }

    public Message(String author, String text) {
        this.author = author;
        this.text = text;
    }

    @Override
    public String toString() {
        return String.format("Message{time=%d; text=\"%s\"; author=\"%s\"}", time, text, author);
    }

    public long getTime() {
        return time;
    }

    public String getText() {
        return text;
    }

    public String getAuthor() {
        return author;
    }
}
