package ru.track.prefork;

import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.ProtocolException;

public class BinaryProtocol<T extends Serializable> implements Protocol<T> {
    static final Logger log = LoggerFactory.getLogger(BinaryProtocol.class);

    @Override
    public byte[] encode(T message) throws IOException {
        //log.info ("encode:" + message);
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try (ObjectOutputStream objectOutputStream = new ObjectOutputStream(bos)) {
            objectOutputStream.writeObject(message);
            return bos.toByteArray();
        } catch (IOException ex) {
            throw new ProtocolException("failed to encode" + message);
        }
    }

    @Nullable
    @Override
    public T decode(byte[] data) throws ProtocolException {
        ByteArrayInputStream bis = new ByteArrayInputStream(data);
        try (ObjectInputStream objectInputStream = new ObjectInputStream(bis)) {
            T message = (T) objectInputStream.readObject();
            //log.info("decode:" + message);
            return message;
        } catch (Exception ex) {
            throw new ProtocolException("failed to decode");
        }
    }
}
