package ru.track.prefork.protocol;


import org.slf4j.LoggerFactory;
import org.slf4j.Logger;


import java.io.*;


public class JavaSerializationProtocol<T extends Serializable> implements Protocol<T> {

    static final Logger log = LoggerFactory.getLogger(JavaSerializationProtocol.class);

    @Override
    public byte[] encode(T msg) throws ProtocolException{
        log.info("encode:" + msg);
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try (ObjectOutputStream objOut = new ObjectOutputStream(bos)){
            objOut.writeObject(msg);
            return bos.toByteArray();
        } catch (IOException e){
            throw new ProtocolException("encoding failed",e);

        }
    }

    @Override

    public T decode (byte[] bytes) throws ProtocolException{
        ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
        try (ObjectInputStream objIn = new ObjectInputStream(bis)){
            T message = (T) objIn.readObject();
            log.info("decode: "+ message);
            return message;
        } catch (Exception e) {
            throw new ProtocolException("decoding failed",e);
        }
    }
}
