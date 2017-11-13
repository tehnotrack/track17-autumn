package ru.track.prefork;

import com.sun.mail.iap.ByteArray;
import org.jetbrains.annotations.NotNull;

import java.io.Serializable;
import java.util.Arrays;

class Message implements Serializable{
    private long senderId;
//    private byte[] content;
    private int length;
    private String msg;

    public Message (byte[] content, long senderId, int length) {
        this.senderId = senderId;
//        this.content = Arrays.copyOf(content, length);
        this.length = length;
        this.msg = new String(content, 0, length);
    }
    public  Message (Message message) {
        this.senderId = message.senderId;
        this.length = message.length;
//        this.content = message.content.clone();
        this.msg = new String(message.msg);
    }

    @Override
    public String toString() {
        return msg;
    }
    public long senderId() { return senderId; }
}
