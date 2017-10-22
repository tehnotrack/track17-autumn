package ru.track.cypher;

import org.jetbrains.annotations.NotNull;

import java.util.*;

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

        Iterator<Character> domainIterator = domainHist.keySet().iterator();
        Iterator<Character> encryptedIterator = encryptedDomainHist.keySet().iterator();

        while (domainIterator.hasNext() && encryptedIterator.hasNext()) {
            cypher.put(encryptedIterator.next(), domainIterator.next());
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
        StringBuilder sb = new StringBuilder();
        char[] characters = encoded.toLowerCase().toCharArray();
        for (int i = 0; i < encoded.length(); ++i) {
            if (cypher.keySet().contains(characters[i])) {
                characters[i] = cypher.get(characters[i]);
            }
            sb.append(characters[i]);
        }

        return sb.toString();
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
        Map<Character, Integer> priorityMap = new HashMap<>();

        text = text.toLowerCase();
        char[] characters = text.toCharArray();
        for (int i = 0; i < CypherUtil.SYMBOLS.length(); i++) {
            priorityMap.put(CypherUtil.SYMBOLS.charAt(i), 0);
        }

        for (int i = 0; i < text.length(); i++) {
            if (CypherUtil.SYMBOLS.contains(String.valueOf(characters[i]))) {
                priorityMap.put(characters[i], priorityMap.get(characters[i]) + 1);
            }
        }

        Comparator<Character> comparator = (obj1, obj2) -> priorityMap.get(obj2) - priorityMap.get(obj1);
        Map<Character, Integer> sortedMap = new TreeMap<>(comparator);
        sortedMap.putAll(priorityMap);

        return sortedMap;
    }

}