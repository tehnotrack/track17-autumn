package ru.track.prefork;

public class Message {
    private String username;
    private String text;
    private long timestamp;

    public Message(String username, String text, long timestamp) {
        this.username = username;
        this.text = text;
        this.timestamp = timestamp;
    }

    public String getUsername() {
        return username;
    }

    public String getText() {
        return text;
    }

    public long getTimestamp() {
        return timestamp;
    }
}
