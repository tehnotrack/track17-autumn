package ru.track.prefork;

import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

import java.io.*;
import java.net.ProtocolException;

public class BinaryProtocol<T extends Serializable> implements Protocol<T> {

    static final Logger log = LoggerFactory.getLogger(BinaryProtocol.class);

    @Override
    public byte[] encode(T msg) throws ProtocolException {
        log.info("encode:" + msg);
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try (ObjectOutputStream oos = new ObjectOutputStream(bos)) {
            oos.writeObject(msg);
            return bos.toByteArray();
        } catch (IOException e) {
            throw new ProtocolException();
        }
    }

    @Override
    public T decode(byte[] bytes) throws ProtocolException {
        ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
        try (ObjectInputStream ois = new ObjectInputStream(bis)) {
            T message = (T) ois.readObject();
            log.info("decode: " + message);
            return message;
        } catch (Exception e) {
            throw new ProtocolException();
        }
    }
}
