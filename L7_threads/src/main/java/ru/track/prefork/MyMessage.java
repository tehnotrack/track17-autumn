package ru.track.prefork;

import java.io.Serializable;

class MyMessage implements Serializable {
    public String text;
    public long ts;

    public MyMessage(long ts, String s) {
        this.text = s;
        this.ts = ts;
    }

    @Override
    public String toString() {
        return "text=" + this.text + ",ts=" + this.ts;
    }
}
