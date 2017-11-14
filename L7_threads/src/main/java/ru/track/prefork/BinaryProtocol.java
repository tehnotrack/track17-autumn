package ru.track.prefork;

import org.jetbrains.annotations.Nullable;

import java.io.*;
import java.net.ProtocolException;

public class BinaryProtocol<T extends Serializable> implements Protocol<T> {

    @Override
    public byte[] encode(T msg) throws IOException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try (
                ObjectOutputStream objOut = new ObjectOutputStream(bos);
        ) {
            objOut.writeObject(msg);
            return bos.toByteArray();
        } catch (IOException e) {
            throw new ProtocolException("encoding failed");
        }
    }

    @Nullable
    @Override
    public T decode(byte[] bytes) throws IOException {
        ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
        try (ObjectInputStream objIn = new ObjectInputStream(bis)) {
            T message = (T) objIn.readObject();
            return message;
        } catch (Exception e) {
            throw new ProtocolException("decoding failed");
        }

    }
}
