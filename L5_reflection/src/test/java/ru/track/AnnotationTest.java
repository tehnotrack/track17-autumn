package ru.track;

import org.junit.Assert;
import org.junit.Test;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import ru.track.json.JsonWriter;
import ru.track.json.samples.Bean;
import ru.track.json.samples.Transaction;

/**
 *
 */
public class AnnotationTest {

    Gson gson = new Gson();

    @Test
    public void namedSerializer() throws Exception {
        Transaction tx = new Transaction(1, 1, 1, 1, "A");
        String expected = gson.toJson(tx);
        String actual = JsonWriter.toJson(tx);
        System.out.println(actual);
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void nullableSerializer() throws Exception {
        Gson customGson = new GsonBuilder().serializeNulls().create();
        Bean bean = new Bean();
        String expected = customGson.toJson(bean);
        String actual = JsonWriter.toJson(bean);

        Assert.assertEquals(expected, actual);

    }
}
