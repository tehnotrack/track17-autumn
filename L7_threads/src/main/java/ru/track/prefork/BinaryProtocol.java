package ru.track.prefork;

import java.io.*;

public class BinaryProtocol <T extends Serializable> {

    public byte[] encode (T msg) throws IOException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try (ObjectOutputStream oos = new ObjectOutputStream(bos)) {
            oos.writeObject(msg);
            return bos.toByteArray();
        } catch (IOException e) {
            throw new IOException("Tyt o4ibka",e);
        }
    }
    public T decode (byte[] bytes) throws ClassNotFoundException, IOException {
        ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
        try (ObjectInputStream oin = new ObjectInputStream(bis)) {
            T message = (T) oin.readObject();
            return message;
        }
    }
}
