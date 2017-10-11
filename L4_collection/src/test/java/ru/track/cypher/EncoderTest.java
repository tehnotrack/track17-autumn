package ru.track.cypher;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.junit.Assert;
import org.junit.Test;

public class EncoderTest {

    static final Encoder encoder = new Encoder();

    @Test
    public void generateCypher() throws Exception {
        Map<Character, Character> cypher = CypherUtil.generateCypher();
        Assert.assertNotNull(cypher);
        Assert.assertEquals(CypherUtil.SYMBOLS.length(), cypher.size());

        char[] array = CypherUtil.SYMBOLS.toCharArray();
        Set<Character> keys = cypher.keySet();
        Set<Character> values = new HashSet<>(cypher.values());
        Assert.assertEquals(array.length, values.size());
        for (char anArray : array) {
            Assert.assertTrue(keys.contains(anArray));
            Assert.assertTrue(values.contains(anArray));
        }
    }

    @Test
    public void encodeEmpty() throws Exception {
        final Map<Character, Character> cypher = CypherUtil.generateCypher();
        String origin = "";
        String encrypted = encoder.encode(cypher, origin);
        Assert.assertEquals(origin.length(), encrypted.length());

        origin = "  ";
        encrypted = encoder.encode(cypher, origin);
        Assert.assertEquals(origin.length(), encrypted.length());
    }

    @Test
    public void encodeSymbolCase() throws Exception {
        final Map<Character, Character> cypher = CypherUtil.generateCypher();
        String origin = "AbC";
        String encrypted = encoder.encode(cypher, origin);
        Assert.assertEquals(origin.length(), encrypted.length());

        for (int i = 0; i < origin.length(); i++) {
            Assert.assertEquals(cypher.get(Character.toLowerCase(origin.charAt(i))).charValue(), encrypted.charAt(i));
        }
    }

    @Test
    public void encodeNoLetter() throws Exception {
        final Map<Character, Character> cypher = CypherUtil.generateCypher();
        String origin = " 132,. 12 ";
        String encrypted = encoder.encode(cypher, origin);
        Assert.assertEquals(origin.length(), encrypted.length());
        Assert.assertEquals(origin, encrypted);
    }

}