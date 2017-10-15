package ru.track.cypher;

import java.util.*;
import java.util.TreeMap;
import java.util.Comparator;

import org.jetbrains.annotations.NotNull;

import javax.rmi.CORBA.Util;

public class Decoder {

    // Расстояние между A-Z -> a-z
    public static final int SYMBOL_DIST = 32;

    private Map<Character, Character> cypher;

    /**
     * Конструктор строит гистограммы открытого домена и зашифрованного домена
     * Сортирует буквы в соответствие с их частотой и создает обратный шифр Map<Character, Character>
     *
     * @param domain - текст по кторому строим гистограмму языка
     */
    public Decoder(@NotNull String domain, @NotNull String encryptedDomain) {
        Map<Character, Integer> domainHist = createHist(domain);
        Map<Character, Integer> encryptedDomainHist = createHist(encryptedDomain);
        cypher = new LinkedHashMap<>();
        Iterator<Map.Entry<Character, Integer>> domainit = domainHist.entrySet().iterator();
        Iterator<Map.Entry<Character, Integer>> encryptedit = encryptedDomainHist.entrySet().iterator();
        for (int i = 0; i < CypherUtil.SYMBOLS.length(); ++i) {
            cypher.put(encryptedit.next().getKey(), domainit.next().getKey());
        }

    }

    public Map<Character, Character> getCypher() {
        return cypher;
    }

    /**
     * Применяет построенный шифр для расшифровки текста
     *
     * @param encoded зашифрованный текст
     * @return расшифровка
     */
    @NotNull
    public String decode(@NotNull String encoded) {
        StringBuilder res = new StringBuilder();
        for (int i = 0; i < encoded.length(); ++i) {
            char c = encoded.charAt(i);
            if (cypher.keySet().contains(c)) c = cypher.get(c);
            res.append(c);
        }

        return res.toString();
    }

    /**
     * Считывает входной текст посимвольно, буквы сохраняет в мапу.
     * Большие буквы приводит к маленьким
     *
     * @param text - входной текст
     * @return - мапа с частотой вхождения каждой буквы (Ключ - буква в нижнем регистре)
     * Мапа отсортирована по частоте. При итерировании на первой позиции наиболее частая буква
     */
    @NotNull
    Map<Character, Integer> createHist(@NotNull String text) {
        //Construct frequency map; It is needed for comparator constructing
        Map<Character, Integer> freq = new HashMap<>();
        for (int i = 0; i < CypherUtil.SYMBOLS.length(); ++i) {
            freq.put(CypherUtil.SYMBOLS.charAt(i), 0);
        }
        for (int i = 0; i < text.length(); ++i) {
            char c = Character.toLowerCase(text.charAt(i));
            if (CypherUtil.SYMBOLS.contains(Character.toString(c))) {
                freq.put(c, freq.get(c) + 1);
            }
        }

        Comparator<Character> comp = new Comparator<Character>() {
            @Override
            public int compare(Character o1,
                               Character o2) {
                int count_diff = freq.get(o2) - freq.get(o1);
                if (count_diff != 0) return count_diff;
                return o2.compareTo(o1);
            }
        };
        Map<Character, Integer> res = new TreeMap<Character, Integer>(comp);
        res.putAll(freq);
        return res;
    }

}
