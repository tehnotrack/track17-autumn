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

        Iterator<Map.Entry<Character, Integer>> valIterator = domainHist.entrySet().iterator();
        Iterator<Map.Entry<Character, Integer>> keyIterator = encryptedDomainHist.entrySet().iterator();

        while(keyIterator.hasNext() && valIterator.hasNext()) {
            cypher.put(keyIterator.next().getKey(), valIterator.next().getKey());
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
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < encoded.length(); i++) {
            char ch = encoded.charAt(i);
            ch = Character.isLetter(ch)
                    ? cypher.get(Character.toLowerCase(ch))
                    : ch;
            result.append(ch);
        }
        return result.toString();
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
        Map<Character, Integer> unsortedHist = new HashMap<>();

        for (int i = 0; i < text.length(); i++) {
            char ch = Character.toLowerCase(text.charAt(i));
            if(Character.isLetter(ch)) {
                unsortedHist.put(ch, unsortedHist.getOrDefault(ch, 0) + 1);
            }
        }

        List<Map.Entry<Character, Integer>> list = new ArrayList<>(unsortedHist.entrySet());
        list.sort(Map.Entry.<Character, Integer>comparingByValue().reversed());

        Map<Character, Integer> sortedHist = new LinkedHashMap<>();
        for (Map.Entry<Character, Integer> entry : list) {
            sortedHist.put(entry.getKey(), entry.getValue());
        }
        return sortedHist;
    }
}
