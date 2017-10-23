package ru.track.cypher;

import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.stream.Collectors;

public class Decoder {

    // Расстояние между A-Z -> a-z
    public static final int SYMBOL_DIST = 32;

    private Map<Character, Character> cypher;

    /**
     * Конструктор строит гистограммы открытого домена и зашифрованного домена
     * Сортирует буквы в соответствие с их частотой и создает обратный шифр Map<Character, Character>
     *
     * @param domain - текст по которому строим гистограмму языка
     */
    public Decoder(@NotNull String domain, @NotNull String encryptedDomain) {
        Map<Character, Integer> domainHist = createHist(domain);
        Map<Character, Integer> encryptedDomainHist = createHist(encryptedDomain);
        Map.Entry<Character, Character>[] enc = new Map.Entry[encryptedDomainHist.size()];
        encryptedDomainHist.entrySet().toArray(enc);
        Map.Entry<Character, Character>[] dom = new Map.Entry[domainHist.size()];
        domainHist.entrySet().toArray(dom);
        cypher = new LinkedHashMap<>();
        for (int i = 0; i < domainHist.size(); i++) {
            cypher.put(enc[i].getKey(), dom[i].getKey());
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
        Map<Character, Character> cypher = this.getCypher();
        StringBuilder decoded = new StringBuilder(encoded);
        for (int i = 0; i < encoded.length(); i++) {
            char curChar = decoded.charAt(i);
            if (!Character.isLetter(curChar)) {
                continue;
            }
            decoded.setCharAt(i, cypher.get(curChar));
        }
        return decoded.toString();
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
        Map<Character, Integer> hist = new HashMap<>(26);
        text = text.toLowerCase();
        for (int i = 0; i < text.length(); i++) {
            char curChar = text.charAt(i);
            if (Character.isLetter(curChar)) {
                int curVal = hist.getOrDefault(curChar, 0);
                hist.put(curChar, ++curVal);
            }
        }

        return hist.entrySet().stream()
                .sorted(Map.Entry.<Character, Integer>comparingByValue().reversed())
                .collect(Collectors.toMap(
                        Map.Entry::getKey, Map.Entry::getValue,
                        (oldValue, newValue) -> oldValue,
                        LinkedHashMap::new));
    }

}
