package ru.track.prefork;

import java.io.Serializable;

public class Message implements Serializable {
    private String text;
    private Integer len;

    Message(String text) {
        this.text = text;
        this.len = text.length();
    }

    String getText() {
        return this.text;
    }
}
