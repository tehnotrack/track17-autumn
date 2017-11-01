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

        Object[] domainHistKeyArray = domainHist.keySet().toArray();
        Object[] encryptedDomainHistKeySet = encryptedDomainHist.keySet().toArray();

        int length;
        if (domainHistKeyArray.length != encryptedDomainHistKeySet.length) {
            return;
        } else {
            length = domainHistKeyArray.length;
        }

        for (int i = 0; i < length; ++i) {
            cypher.put((Character) encryptedDomainHistKeySet[i], (Character) domainHistKeyArray[i]);
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
        StringBuilder decoded = new StringBuilder();

        for (int i = 0; i < encoded.length(); ++i) {
            char c = encoded.charAt(i);
            if (Character.isLetter(c)) {
                decoded.append(cypher.get(c));
            } else {
                decoded.append(c);
            }
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
        Map<Character, Integer> hist = new HashMap<>();

        for (int i = 0; i < text.length(); ++i) {
            char c = Character.toLowerCase(text.charAt(i));
            if (!Character.isLetter(c)) {
                continue;
            }

            Integer char_count = hist.get(c);
            if (char_count != null) {
                hist.put(c, char_count + 1);
            } else {
                hist.put(c, 1);
            }
        }

        List<Map.Entry<Character, Integer>> list = new LinkedList<>(hist.entrySet());
        list.sort((Map.Entry<Character, Integer> o1, Map.Entry<Character, Integer> o2) -> o2.getValue() - o1.getValue());

        Map<Character, Integer> result = new LinkedHashMap<>();
        for (Map.Entry<Character, Integer> entry : list) {
            result.put(entry.getKey(), entry.getValue());
        }

        return result;
    }

}
