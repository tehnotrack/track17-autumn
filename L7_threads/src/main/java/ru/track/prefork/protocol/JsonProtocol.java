package ru.track.prefork.protocol;

import com.google.gson.Gson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;


public class JsonProtocol<T extends Serializable> implements Protocol<T> {
    static final Logger log = LoggerFactory.getLogger(JsonProtocol.class);
    private static final Gson gson = new Gson();

    @Override
    public byte[] encode(T msg) throws ProtocolException {
        log.info("encode" + msg);
        return gson.toJson(msg).getBytes();
    }

    @Override
    public T decode(byte[] bytes, Class<T> clazz) throws ProtocolException {
        try {
            T message = (T) gson.fromJson(new String(bytes), clazz);
            return message;
        } catch (Exception e) {
            throw new ProtocolException("decoding failed", e);
        }
    }
}
