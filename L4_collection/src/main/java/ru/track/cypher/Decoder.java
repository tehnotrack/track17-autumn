package ru.track.cypher;

import java.util.*;

import org.jetbrains.annotations.NotNull;

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
        Iterator<Character> keys = domainHist.keySet().iterator();
        Iterator<Character> vals = encryptedDomainHist.keySet().iterator();
        while (keys.hasNext()) {
            cypher.put(vals.next(), keys.next());
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
        for (int i = 0; i < encoded.length(); i++) {
            if (Character.isLetter(encoded.charAt(i))) {
                res.append(cypher.get(Character.toLowerCase(encoded.charAt(i))));
            } else {
                res.append(encoded.charAt(i));
            }
        }
        return res.toString();
    }

    /**
     * Считывает входной текст посимвольно, буквы сохраняет в мапу.
     * Большие буквы приводит к маленьким
     *
     *
     * @param text - входной текст
     * @return - мапа с частотой вхождения каждой буквы (Ключ - буква в нижнем регистре)
     * Мапа отсортирована по частоте. При итерировании на первой позиции наиболее частая буква
     */

    private Map<Character, Integer> sortByValue(Map<Character, Integer> unsortedMap) {
        List<Map.Entry<Character, Integer>> list =
                new LinkedList<>(unsortedMap.entrySet());
        Collections.sort(list, (Map.Entry<Character, Integer> o1,
                                Map.Entry<Character, Integer> o2) -> (o2.getValue()).compareTo(o1.getValue()));
        Map<Character, Integer> sortedMap = new LinkedHashMap<>();
        for (Map.Entry<Character, Integer> entry : list) {
            sortedMap.put(entry.getKey(), entry.getValue());
        }
        return sortedMap;
    }

    @NotNull
    public Map<Character, Integer> createHist(@NotNull String text) {
        Map<Character, Integer> hist = new LinkedHashMap<>();
        for (int i = 0; i < CypherUtil.SYMBOLS.length(); i++) {
            hist.put(CypherUtil.SYMBOLS.charAt(i), 0);
        }
        for (int i = 0; i < text.length(); i++) {
            if (Character.isLetter(text.charAt(i))) {
                Character symbol = Character.toLowerCase(text.charAt(i));
                hist.put(symbol, hist.get(symbol) + 1);
            }
        }
        hist = sortByValue(hist);
        return hist;
    }

}