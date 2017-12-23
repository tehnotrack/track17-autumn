package ru.track.prefork;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Message {
    private String username;
    private String text;
    private long   timestamp;
    
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
    
    public String getFormattedTimestamp() {
        Date       date      = new Date(timestamp);
        DateFormat formatter = new SimpleDateFormat("d.M.Y HH:mm:ss");
        
        return formatter.format(date);
    }
}
