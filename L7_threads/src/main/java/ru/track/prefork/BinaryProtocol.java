package ru.track.prefork;

import java.io.*;

public class BinaryProtocol<T extends Serializable> implements Protocol<T> {

    @Override
    public byte[] encode(T msg) throws ProtocolException, IOException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try (ObjectOutputStream oos = new ObjectOutputStream(bos)) {
            oos.writeObject(msg);
            return bos.toByteArray();
        } catch (IOException e) {
            throw new ProtocolException("encoding failed", e);
        }
    }

    @Override
    public T decode(byte[] data) throws ProtocolException, IOException {
        ByteArrayInputStream bis = new ByteArrayInputStream(data);
        try (ObjectInputStream ois = new ObjectInputStream(bis)) {
            return (T) ois.readObject();
        } catch (ClassNotFoundException e) {
            throw new ProtocolException("decoding failed", e);
        }
    }
}
