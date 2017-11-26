package ru.track.prefork;

import org.jetbrains.annotations.Nullable;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Message implements Serializable {
    String text;
    String date;
    String username;

    public Message (String text, String username) {
        this.text = text;
        this.date = getDate();
        this.username = username;
    }

    public Message (String text) {
        this (text, "");
    }

    public String getDate () {
        String pattern = "yyyy-MM-dd HH:mm:ss";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
        String date = simpleDateFormat.format(new Date());
        return date;
    }
    @Override
    public String toString() {
        return String.format("%s -- %s > %s", date, username, text);
    }
}
