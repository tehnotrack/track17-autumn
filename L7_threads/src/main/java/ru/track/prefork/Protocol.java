package ru.track.prefork;

import java.io.IOException;
import java.io.Serializable;
import javax.xml.ws.ProtocolException;


public interface Protocol<T extends Serializable> {

    byte[] encode(T msg) throws ProtocolException, IOException;

    T decode(byte[] data) throws ProtocolException, IOException;
}

