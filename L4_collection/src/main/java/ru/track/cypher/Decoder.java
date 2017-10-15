package ru.track.cypher;

import java.util.*;
import java.util.function.Predicate;

import org.jetbrains.annotations.NotNull;

public class Decoder {

    // Расстояние между A-Z -> a-z
    public static final int SYMBOL_DIST = 32;

    //
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

        Iterator<Map.Entry<Character, Integer>> itdom = domainHist.entrySet().iterator();
        Iterator<Map.Entry<Character, Integer>> itencr = encryptedDomainHist.entrySet().iterator();
        while (itdom.hasNext() && itencr.hasNext())
        {
            Map.Entry<Character, Integer> pair = itdom.next();
            Map.Entry<Character, Integer> pair2 = itencr.next();
            cypher.put(pair2.getKey(), pair.getKey());

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
        for (int i = 0; i < encoded.length(); i++)
        {
            Character c = encoded.charAt(i);
            if (cypher.containsKey(c))
                str.append(cypher.get(c));
            else
                str.append(c);
        }
        return str.toString();
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
        text = text.toLowerCase();
        Map<Character, Integer> freqmap = new HashMap<>();

        for (int i = 0; i < text.length(); i++) {
            char ch = text.charAt(i);
            if (Character.isLetter(ch)) {
                Integer count = freqmap.get(ch);
                if (count == null) {
                    freqmap.put(ch, 1);
                } else {
                    freqmap.put(ch, count + 1);
                }
            }
        }

        Map<Character, Integer> sortedmap = new LinkedHashMap<>();
        List<Map.Entry<Character, Integer>> sortedlist = new ArrayList<>(freqmap.entrySet());
        Collections.sort(sortedlist, (o1, o2) -> o2.getValue() - o1.getValue());
        for (Map.Entry<Character, Integer> entry: sortedlist)
            sortedmap.put(entry.getKey(), entry.getValue());

        return sortedmap;
    }
}


