package ru.track.prefork.Protocol;

import com.sun.mail.iap.ByteArray;
import org.jetbrains.annotations.NotNull;

import java.io.Serializable;
import java.util.Arrays;

public class Message implements Serializable{
    private long senderId;
    private int length;
    private String msg;

    public Message (byte[] content, long senderId, int length) {
        this.senderId = senderId;
        this.length = length;
        this.msg = new String(content, 0, length);
    }

    public  Message (Message message) {
        this.senderId = message.senderId;
        this.length = message.length;
        this.msg = new String(message.msg);
    }

    @Override
    public String toString() { return msg; }

    public long senderId() { return senderId; }
}