package ru.track.prefork.protocol;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 */
public class JavaSerializationProtocol<T extends Serializable> implements Protocol<T> {
    static final Logger log = LoggerFactory.getLogger(JavaSerializationProtocol.class);

    @Override
    public byte[] encode(T obj) throws ProtocolException {
        log.info("encode:" + obj);
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try (ObjectOutputStream objOut = new ObjectOutputStream(bos)) {
            objOut.writeObject(obj);
            return bos.toByteArray();
        } catch (IOException e) {
            throw new ProtocolException("encoding failed", e);
        }
    }

    @Override
    public T decode(byte[] bytes) throws ProtocolException {
        ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
        try (ObjectInputStream objIn = new ObjectInputStream(bis)) {
            T message = (T) objIn.readObject();
            log.info("decode: " + message);
            return message;
        } catch (Exception e) {
            throw new ProtocolException("decoding failed", e);
        }
    }
}
