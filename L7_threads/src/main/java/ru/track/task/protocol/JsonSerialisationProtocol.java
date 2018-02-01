package ru.track.task.protocol;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;

/**
 *
 */
public class JsonSerialisationProtocol implements Protocol {
    static final Logger log = LoggerFactory.getLogger(JsonSerialisationProtocol.class);

    @Override
    public byte[] encode(Message obj) throws ProtocolException {
        log.info("encode:" + obj);
        Gson gson = new Gson();

        log.info("encoded as: " + gson.toJson(obj));

        return gson.toJson(obj).getBytes();
    }

    @Override
    public Message decode(byte[] bytes) throws ProtocolException {
        String json = new String(bytes);
        log.info("decode:" + json);


        JsonReader reader = new JsonReader(new StringReader(json));
        reader.setLenient(true);

        JsonParser parser = new JsonParser();

        JsonElement element = parser.parse(reader);

        try {
            if (element.isJsonObject()) {
                JsonObject msg = element.getAsJsonObject();
                Message res = new Message(msg.get("ts").getAsLong(),
                        msg.get("text").getAsString());
                res.username = msg.get("username").getAsString();

                return res;
            } else {
                throw new ProtocolException("could not decode");
            }
        } catch (Exception e) {
            throw new ProtocolException(e.getMessage());
        }
    }
}
