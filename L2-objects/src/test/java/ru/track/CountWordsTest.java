package ru.track;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

/**
 *
 */
@Ignore
public class CountWordsTest {

    static File file;

    @BeforeClass
    public static void init() {
        file = new File("words.txt");
    }

    @Test
    public void countNumbers() throws Exception {

        CountWords cw = new CountWords("vu53f28MvpQ4PclHvxHZ");
        long number = cw.countNumbers(file);
        String result = cw.concatWords(file);


        BufferedReader reader = new BufferedReader(new FileReader(new File("words_result.txt")));
        StringBuilder expected = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            expected.append(line + " ");
        }

        Assert.assertEquals("String concat: ", expected.toString(), result);
        Assert.assertEquals("Integer sum: ", (long) Integer.MAX_VALUE + 1, number);

    }

}