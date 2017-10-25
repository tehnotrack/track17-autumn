package ru.track;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import com.google.gson.Gson;
import ru.track.beans.Car;
import ru.track.beans.Engine;

/**
 *
 */
public class SerializerTest {

    private static Car car;
    private static Engine engine;

    @BeforeClass
    public static void setUp() throws Exception {
        engine = new Engine(100);
        car = new Car(1, "A", engine);
    }

    /**
     * Сериализация в массив байт
     * @throws Exception
     */
    @Test
    public void toMemory() throws Exception {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(out);
        oos.writeObject(car);

        byte[] data = out.toByteArray();

        ByteArrayInputStream in = new ByteArrayInputStream(data);
        ObjectInputStream ois = new ObjectInputStream(in);
        Car carNew = (Car) ois.readObject();

        Assert.assertEquals(car, carNew);
    }

    /**
     * Сериализация в файл
     * @throws Exception
     */
    @Test
    public void toFile() throws Exception {
        File dir = new File(getClass().getClassLoader().getResource("").getPath());
        File tmp = File.createTempFile("L5-serialization", ".binary", dir);

        ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(tmp));
        oos.writeObject(car);

        ObjectInputStream ois = new ObjectInputStream(new FileInputStream(tmp));
        Car carNew = (Car) ois.readObject();

        Assert.assertEquals(car, carNew);
    }

    /**
     * Сериализация/десериализация в json
     * Текстовый формат
     * @throws Exception
     */
    @Test
    public void toJson() throws Exception {
        Gson gson = new Gson();
        String result = gson.toJson(car);
        Assert.assertEquals("{\"id\":1,\"model\":\"A\",\"engine\":{\"power\":100}}", result);
        System.out.println(result);

        Car carNew = gson.fromJson(result, Car.class);
        Assert.assertEquals(car, carNew);
    }
}
