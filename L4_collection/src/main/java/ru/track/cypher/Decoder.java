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

    private Map<Character, Integer> sortMap(Map<Character, Integer> map) {
        Map<Character, Integer> result = new LinkedHashMap<>();
        List<Map.Entry<Character, Integer>> list = new LinkedList<>(map.entrySet());
        Collections.sort(list, new Comparator<Map.Entry<Character, Integer>>() {
            @Override
            public int compare(Map.Entry<Character, Integer> o1, Map.Entry<Character, Integer> o2) {
                return o2.getValue() - o1.getValue();
            }
        });
        for(Map.Entry<Character, Integer> entry : list) {
            result.put(entry.getKey(), entry.getValue());
        }
        return result;
    }

    public Decoder(@NotNull String domain, @NotNull String encryptedDomain) {
        Map<Character, Integer> domainHist = createHist(domain);
        Map<Character, Integer> encryptedDomainHist = createHist(encryptedDomain);

        cypher = new LinkedHashMap<>();

        Iterator<Character> it2 = domainHist.keySet().iterator();
        for(Iterator<Character> it1 = encryptedDomainHist.keySet().iterator(); it1.hasNext(); ) {
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
        StringBuilder result = new StringBuilder();
        for(int i = 0; i < encoded.length(); i++) {
            Character c = encoded.charAt(i);
            c = Character.toLowerCase(c);
            if((int)c >= 97 && (int)c <= 122) {
                result.append(cypher.get(c));
            }
            else {
                result.append(c);
            }
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
        Map<Character, Integer> map = new LinkedHashMap<>();
        for(int i = 0; i < text.length(); i++) {
            Character c = text.charAt(i);
            c = Character.toLowerCase(c);
            if(c >= 'a' && c <= 'z') {
                if (map.get(c) != null) {
                    int count = map.get(c);
                    map.put(c, count + 1);
                } else {
                    map.put(c, 1);
                }
            }
        }
        map = sortMap(map);
        return map;
    }



}
