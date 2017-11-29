package ru.track.cypher;

import java.util.*;

import org.jetbrains.annotations.NotNull;

public class Decoder {

    public static final int SYMBOL_DIST = 32;

    private Map<Character, Character> cypher;

    public Decoder(@NotNull String domain, @NotNull String encryptedDomain) {

        Map<Character, Integer> domainHist = createHist(domain);
        Map<Character, Integer> encryptedDomainHist = createHist(encryptedDomain);

        Set<Character> v = domainHist.keySet();
        Character values [] = v.toArray(new Character[v.size()]);
        Set<Character> k = encryptedDomainHist.keySet();
        Character keys [] = k.toArray(new Character[k.size()]);
        cypher = new LinkedHashMap<>();

        for (int i = 0; i < keys.length; i++){

            cypher.put(keys[i],values[i]);
        }
    }

    public Map<Character, Character> getCypher() {
        return cypher;
    }

    @NotNull
    public String decode(@NotNull String encoded) {

        StringBuilder temp = new StringBuilder();
        encoded = encoded.toLowerCase();

        for (int i = 0; i < encoded.length(); i++) {

            if (getCypher().containsKey(encoded.charAt(i))) {

                temp.append(getCypher().get(encoded.charAt(i)));
            } else {

                temp.append(encoded.charAt(i));
            }
        }

        return temp.toString();
    }

    @NotNull
    Map<Character, Integer> createHist(@NotNull String text) {

        text = text.toLowerCase();
        Map<Character, Integer> hist = new LinkedHashMap<>();

        for (int i = 0; i < text.length() - 1; i++){
            if (Character.isLetter(text.charAt(i))) {

                if (!hist.containsKey(text.charAt(i))) {

                    hist.put(text.charAt(i), 1);
                } else {

                    hist.replace(text.charAt(i), hist.get(text.charAt(i)) + 1);
                }
            }
        }

        Map<Integer, Character> tmp = new TreeMap<>(Collections.reverseOrder());

        for (char c : hist.keySet()) {

            tmp.put(hist.get(c),c);
        }

        hist = new LinkedHashMap<>();

        for (int i : tmp.keySet()) {

            hist.put(tmp.get(i),i);
        }

        return hist;
    }

}
