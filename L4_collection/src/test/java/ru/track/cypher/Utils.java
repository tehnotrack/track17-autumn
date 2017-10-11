package ru.track.cypher;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 *
 */
public class Utils {
    public static String readFromResources(String resourceName) throws IOException {
        ClassLoader classLoader = DecoderTest.class.getClassLoader();
        Path path = new File(classLoader.getResource(resourceName).getFile()).toPath();
        StringBuilder builder = new StringBuilder();
        Files.readAllLines(path).forEach(builder::append);
        return builder.toString();
    }

    public static String readFromFile(File file) throws IOException {
        Path path = file.toPath();
        StringBuilder builder = new StringBuilder();
        Files.readAllLines(path).forEach(builder::append);
        return builder.toString();
    }

    public static void writeToResource(String resourceName, String data) throws IOException {
        ClassLoader classLoader = DecoderTest.class.getClassLoader();
        File file = new File(classLoader.getResource(resourceName).getFile());

        FileWriter fw = new FileWriter(file);
        fw.write(data);
        fw.flush();
        fw.close();
    }

}
