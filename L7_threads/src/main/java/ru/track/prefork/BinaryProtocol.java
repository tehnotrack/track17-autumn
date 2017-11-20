package ru.track.prefork;

import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.ProtocolException;

public class BinaryProtocol<T extends Serializable> implements Protocol<T> {
    static private final Logger log = LoggerFactory.getLogger(BinaryProtocol.class);
    @Override
    public byte[] encode(T msg) throws ProtocolException, IOException {
//        log.info("encode:" + msg);
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try (ObjectOutputStream objOut = new ObjectOutputStream(bos)) {
            objOut.writeObject(msg);
            return bos.toByteArray();
        } catch (IOException e) {
            throw new ProtocolException("encoding failed");
        }
    }

    @Nullable
    @Override
    public T decode(byte[] data) throws ProtocolException, IOException {
        ByteArrayInputStream bis = new ByteArrayInputStream(data);
        try (ObjectInputStream objIn = new ObjectInputStream(bis)){
            T message = (T) objIn.readObject();
            log.info(String.format("decode \"%s\"", message.toString()));
            return message;
        } catch (ClassNotFoundException e) {
            throw new ProtocolException("decoding failed");
        }
    }
}
