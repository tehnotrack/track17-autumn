package ru.track.cypher;

import java.util.*;
import java.util.stream.Collectors;

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

        Iterator<Character> it1 = domainHist.keySet().iterator(),
                            it2 = encryptedDomainHist.keySet().iterator();

        while(it1.hasNext() && it2.hasNext()) {
            cypher.put(it2.next(), it1.next());
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
        return encoded.
                chars().
                boxed().
                map((Integer i) -> (char) i.intValue()).
                map(c -> cypher.containsKey(c) ? cypher.get(c) : c).
                map((Character c) -> c.toString()).
                collect(Collectors.joining());
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
        Map<Character, Integer> res = new HashMap<>();
        Character c;

        for (char i='a'; i <= 'z'; i++) {
            res.put(i, 0);
        }

        for(int i=0;i<text.length();i++) {
            c = text.charAt(i);
            c = Character.toLowerCase(c);
            if ('a' <= c && c <= 'z') {
                res.replace(c, res.get(c) + 1);
            }
        }

        LinkedHashMap<Character, Integer> sortedMap =
                res.entrySet().stream().
                        sorted(Map.Entry.comparingByValue(Collections.reverseOrder())).
                        collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue,
                                (e1, e2) -> e1, LinkedHashMap::new));

        return sortedMap;
    }

}
