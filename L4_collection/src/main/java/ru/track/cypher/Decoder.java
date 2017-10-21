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

        Iterator<Character> domainOrderedLetters = domainHist.keySet().iterator();
        Iterator<Character> encryptedOrderedLetters = encryptedDomainHist.keySet().iterator();

        while (domainOrderedLetters.hasNext() && encryptedOrderedLetters.hasNext()) {
            cypher.put(encryptedOrderedLetters.next(), domainOrderedLetters.next());
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
        char[] text = encoded.toCharArray();
        for (int i = 0; i < encoded.length(); i++) {
            text[i] = cypher.getOrDefault(text[i], text[i]);
        }
        return new String(text);
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
    @NotNull
    Map<Character, Integer> createHist(@NotNull String text) {
        Map<Character, Integer> counter = new HashMap<>();
        char[] textArray = text.toLowerCase().toCharArray();

        for (char letter : textArray) {
            if (Character.isLetter(letter)) {
                counter.put(letter, counter.getOrDefault(letter, 0) + 1);
            }
        }

        List<Map.Entry<Character, Integer>> list = new ArrayList<>(counter.entrySet());
        list.sort((Map.Entry<Character, Integer> left, Map.Entry<Character, Integer> right) -> {
            return (right.getValue().compareTo(left.getValue()));
        });

        Map<Character, Integer> histMap = new LinkedHashMap<>();
        for (Map.Entry<Character, Integer> entry : list) {
            histMap.put(entry.getKey(), entry.getValue());
        }

        return histMap;
    }

}
