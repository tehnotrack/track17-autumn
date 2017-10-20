package ru.track.cypher;

import java.util.*;

import org.jetbrains.annotations.NotNull;

public class Decoder {

    // Расстояние между A-Z -> a-z
    public static final int SYMBOL_DIST = 32;
    public static final int alphabetSize = 26;

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
        Set<Character> domainHistKeys = domainHist.keySet();
        Set<Character> encryptedDomainHistKeys = encryptedDomainHist.keySet();
        for (Iterator<Character> it1 = encryptedDomainHistKeys.iterator(),
             it2 = domainHistKeys.iterator(); it1.hasNext() && it2.hasNext(); ) {
            cypher.put(it1.next(), it2.next());
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
            if (cypher.containsKey(encoded.charAt(i))) {
                sb.append(cypher.get(encoded.charAt(i)));
            } else {
                sb.append(encoded.charAt(i));
            }
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
        Map<Character, Integer> map = new LinkedHashMap<>(alphabetSize);
        for (char i = 'a'; (int) i <= (int) 'z'; i++) {
            map.put(i, 0);
        }

        for (int i = 0; i < text.length(); i++) {
            if (!Character.isLetter(text.charAt(i)))
                continue;

            char ch = text.charAt(i);
            if (Character.isUpperCase(text.charAt(i)))
                ch = Character.toLowerCase(ch);

            Integer temp = map.get(ch);
            if (temp != null)
                map.replace(ch, temp + 1);
        }

        List<Map.Entry<Character, Integer>> list = new ArrayList<>(map.entrySet());
        list.sort((o1, o2) -> o2.getValue() - o1.getValue());
        Map<Character, Integer> sortedMap = new LinkedHashMap<>();
        for (Map.Entry<Character, Integer> entry : list) {
            sortedMap.put(entry.getKey(), entry.getValue());
        }

        return sortedMap;
    }

}
