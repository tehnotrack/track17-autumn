package ru.track.prefork;

import java.io.*;

public class Message implements Serializable {

    private long ts;
    private final String data;

    Message (String msg) {
        data = msg;
    }

    public String getData() {
        return data;
    }
}
