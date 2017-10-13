package ru.track.cypher;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

public class DecoderTest {

    // Отсортированные в порядке частоты символы английского алфавита (На основе resource/domain.txt)
    static final List<Character> SYMBOLS = Arrays.asList(
            'e','t','o','a','s','i','n','h','r','l','d','u','m',
            'f','c','w', 'y','b','g','p','v','k','j','q','x','z');

    static String domain;
    static String encoded;
    static Decoder decoder;
    static Encoder encoder;

    @BeforeClass
    public static void init() throws IOException {
        domain = Utils.readFromResources("domain.txt");
        encoded = Utils.readFromResources("encrypted.txt");

        decoder = new Decoder(domain, encoded);
        encoder = new Encoder();
    }


    @Test
    public void createHist() throws Exception {

        Map<Character, Integer> hist = decoder.createHist(domain);

        Assert.assertNotNull("Hist cannot be null", hist);
        Assert.assertEquals(26, hist.size());

        List<Character> keys = new ArrayList<>(hist.keySet());
        Assert.assertEquals("Hist origin", SYMBOLS, keys);
    }

    @Test
    public void simple() throws Exception {
        final Map<Character, Character> cypher = CypherUtil.generateCypher();
        System.out.println("Cypher: " + cypher);

        // Шифруем secret текст
        String secret = Utils.readFromResources("secret.txt");
        String encoded = encoder.encode(cypher, secret);
        Decoder decoder = new Decoder(domain, encoded);

        // пытаемся расшифровать сообщение
        String testEncoded = encoder.encode(cypher, "e to a hi s!");
        String result = decoder.decode(testEncoded);
        Assert.assertEquals("e to a ih s!", result);
    }
}