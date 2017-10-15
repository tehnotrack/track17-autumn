package ru.track.cypher;

import java.util.*;
import java.util.function.Predicate;

import com.sun.org.apache.regexp.internal.CharacterArrayCharacterIterator;
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

        Iterator<Map.Entry<Character, Integer>> DomainHistIt = domainHist.entrySet().iterator();
        Iterator<Map.Entry<Character, Integer>> EncryptedDomainHistIt = encryptedDomainHist.entrySet().iterator();

        while (DomainHistIt.hasNext() & EncryptedDomainHistIt.hasNext()) {
            Map.Entry<Character, Integer> DomainPair = DomainHistIt.next();
            Map.Entry<Character, Integer> EncryptedDomainPair = EncryptedDomainHistIt.next();
            cypher.put(EncryptedDomainPair.getKey(), DomainPair.getKey());
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

        for (int i = 0; i < encoded.length(); i++) {
            Character c = encoded.charAt(i);
            if (cypher.containsKey(c))
                decoded.append(cypher.get(c));
            else
                decoded.append(c);
        }

        return decoded.toString();
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

        Map<Character, Integer> myMap =new HashMap<>();

        for (int i = 0; i < 26; i++)
            myMap.put((char) ('a' + i) , 0);

        for (int i = 0; i < text.length(); i++) {
            Character c = text.charAt(i);
            if (myMap.containsKey(c)) {
                if (myMap.get(c) == null) {
                    myMap.put(c, 1);
                } else {
                    myMap.put(c, myMap.get(c) + 1);
                }
            }
        }

        return sortbyValue(myMap);
    }

    @NotNull
    Map<Character, Integer> sortbyValue (Map<Character, Integer> mp) {
        List<Map.Entry<Character, Integer>> list = new LinkedList<Map.Entry<Character, Integer>>(mp.entrySet());

        Collections.sort(list, new Comparator<Map.Entry<Character, Integer>>() {
            public int compare(Map.Entry<Character, Integer> o1,
                               Map.Entry<Character, Integer> o2) {
                return (o2.getValue()).compareTo(o1.getValue());
            }
        });

        Map<Character, Integer> SortedMap = new LinkedHashMap<>();

        for (Map.Entry<Character, Integer> entry : list)
            SortedMap.put(entry.getKey(), entry.getValue());

        return SortedMap;
    }

}
