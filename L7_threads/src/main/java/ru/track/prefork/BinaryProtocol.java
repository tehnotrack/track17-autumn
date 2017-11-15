package ru.track.prefork;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.ws.ProtocolException;
import java.io.*;

class BinaryProtocol<T extends Serializable> implements Protocol<T> {
    private Logger log;

    public BinaryProtocol() {
        log = LoggerFactory.getLogger(BinaryProtocol.class);
    }

    @Override
    public byte[] encode(T msg) throws ProtocolException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try(ObjectOutputStream oos = new ObjectOutputStream(bos)) {
            oos.writeObject(msg);
            log.info("Before encoding: " + msg);
            return bos.toByteArray();
        } catch (IOException e) {
            throw new ProtocolException("encoding faild", e);
        }
    }

    @Override
    public T decode(byte[] data) throws ProtocolException {
        ByteArrayInputStream bis = new ByteArrayInputStream(data);
        try(ObjectInputStream ois = new ObjectInputStream(bis)) {
            T msg = (T) ois.readObject();
            log.info("Decoded: " + msg);
            return msg;
        } catch (Exception e) {
            throw new javax.xml.ws.ProtocolException("Decoding faild", e);
        }
    }
}
