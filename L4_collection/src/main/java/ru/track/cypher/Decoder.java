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

        List<Character> encrypts = new ArrayList<>(encryptedDomainHist.keySet());
        List<Character> domains = new ArrayList<>(domainHist.keySet());
        cypher = new LinkedHashMap<>();
        for (int i = 0; i < domainHist.size(); ++i) {
            cypher.put(encrypts.get(i), domains.get(i));
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
        StringBuilder str = new StringBuilder();
        for (int i = 0; i < encoded.length(); ++i) {
            Character c = Character.toLowerCase(encoded.charAt(i));
            str.append(cypher.getOrDefault(c, c));
        }
        return str.toString();
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
        Map<Character, Integer> ret = new LinkedHashMap<>();
        for (int i = 0; i < text.length(); ++i) {
            char c = Character.toLowerCase(text.charAt(i));
            if (Character.isAlphabetic(c)) {
                ret.put(c, ret.getOrDefault(c, 1) + 1);
            }
        }
        List<Map.Entry<Character, Integer>> lst = new ArrayList<>(ret.entrySet());
        lst.sort((t1, t2) -> t2.getValue() - t1.getValue());
        LinkedHashMap<Character, Integer> sortedMap = new LinkedHashMap<>();
        for (Map.Entry<Character, Integer> entry : lst) {
            sortedMap.put(entry.getKey(), entry.getValue());
        }
        return sortedMap;
    }
}
