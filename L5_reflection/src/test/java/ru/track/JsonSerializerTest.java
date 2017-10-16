package ru.track;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import com.google.gson.Gson;
import ru.track.beans.Car;
import ru.track.beans.Engine;
import ru.track.json.JsonWriter;

/**
 *
 */
@RunWith(Parameterized.class)
public class JsonSerializerTest {

    static class Sample {
        @Nullable
        private Object obj;

        public Sample(@Nullable Object obj) {
            this.obj = obj;
        }

        @Nullable
        public Object getObj() {
            return obj;
        }
    }

    @NotNull
    private final Sample sample;

    private static final Gson gson = new Gson();

    public JsonSerializerTest(@NotNull Sample sample) {
        this.sample = sample;
    }

    @Parameterized.Parameters
    public static Collection<Sample> data() {

        Map<Long, String> map = new HashMap<>();
        map.put(1L, "Abc");
        map.put(2L, "Abc");
        map.put(Long.MAX_VALUE, "X");

        Engine engine = new Engine();
        Car car = new Car(1, "A", engine);

        return Arrays.asList(
                new Sample(null),
                new Sample('w'),
                new Sample("Hello"),
                new Sample(true),
                new Sample(123),
                new Sample(1.23),
                new Sample(new int[]{1, 2, 3}),
                new Sample(new String[]{"A", "B", "C"}),
                new Sample(Arrays.asList("A", "B", "C")),
                new Sample(map),
                new Sample(engine),
                new Sample(car),
                new Sample(Complex.getInstance())
        );
    }

    @Test
    public void toJson() throws Exception {
        final String expected = gson.toJson(sample.getObj());
        final String actual = JsonWriter.toJson(sample.getObj());
        Assert.assertEquals(expected, actual);
    }

    static class Complex {
        int a;
        String s;
        Map<String, List<Integer>> map;
        Car car;

        public Complex(int a, String s, Map<String, List<Integer>> map, Car car) {
            this.a = a;
            this.s = s;
            this.map = map;
            this.car = car;
        }

        static Complex getInstance() {
            Map<String, List<Integer>> map = new HashMap<>();
            map.put("A", Arrays.asList(1, 2, 3));
            map.put("B", Arrays.asList(1, 2, 3, 4));
            Car car = new Car();
            return new Complex(1, "A", map, car);
        }
    }
}
