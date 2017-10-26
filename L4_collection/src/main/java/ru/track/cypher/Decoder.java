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

        Iterator<Character> domainIter = domainHist.keySet().iterator();
        Iterator<Character> encryptedIter = encryptedDomainHist.keySet().iterator();

        while (domainIter.hasNext() && encryptedIter.hasNext()) {
            cypher.put(encryptedIter.next(), domainIter.next());
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

        for (int i = 0; i < encoded.length(); i++) {
            char tmpChar = Character.toLowerCase(encoded.charAt(i));
            if (tmpChar >= 'a' && tmpChar <= 'z') {
                sb.append(cypher.get(tmpChar));
            } else sb.append(tmpChar);
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
        Map<Character, Integer> tmp = new HashMap<>();

        text = text.toLowerCase();

        for (int i = 0; i < 26; i++) {
            tmp.put((char) (Character.valueOf('a') + i), 0);
        }

        for (Character i : text.toCharArray()) {
            if ((i >= 'a') && (i <= 'z')) {
                tmp.put(i, tmp.get(i) + 1);
            }
        }
        List<Map.Entry<Character, Integer>> list = new LinkedList<>(tmp.entrySet());
        Collections.sort(list, (o1, o2) -> (o2.getValue()).compareTo(o1.getValue()));

        Map<Character, Integer> result = new LinkedHashMap<>();
        for (Map.Entry<Character, Integer> entry : list) {
            result.put(entry.getKey(), entry.getValue());
        }
        return result;
    }

}
