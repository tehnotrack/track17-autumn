package ru.track.prefork.protocol;

import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.lang.reflect.Type;

public class JsonProtocol<T extends Serializable> implements Protocol<T> {
    static final Logger log = LoggerFactory.getLogger(JsonProtocol.class);
    private static final Gson gson = new Gson();

    @Override
    public byte[] encode(T msg) throws ProtocolException {
            log.info("encode" + msg);
            return gson.toJson(msg).getBytes();
    }

    @Override
    public T decode(InputStream is, Class<T> clazz) throws ProtocolException {
        try {
            JsonReader reader = new JsonReader(new InputStreamReader(is, "UTF-8"));
//            T message = (T) gson.fromJson(reader, message.getClass());
            T message = (T) gson.fromJson(reader, clazz);
            log.info("decode: " + message);
            return message;
        } catch (Exception e) {
            throw new ProtocolException("decoding failed", e);
        }
    }
}
