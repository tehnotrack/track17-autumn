package ru.track.work_with_database;

public class Message {
    String senderName;
    String text;
    long timestamp;

    public Message(String senderName, String text, long timestamp) {
        this.senderName = senderName;
        this.text = text;
        this.timestamp = timestamp;
    }
}
