package ru.track.cypher;

import java.util.*;

import org.jetbrains.annotations.NotNull;

import static ru.track.cypher.CypherUtil.SYMBOLS;

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
//        Iterator keys = domainHist.entrySet().iterator();
//        Iterator values = encryptedDomainHist.entrySet().iterator();
//        while (keys.hasNext() && values.hasNext()){
//            Map.Entry key = keys.next();
//            cypher.put(key.getKey(), value);
//
//        }

//        Set<Character> keys = domainHist.keySet();
//        Set<Character> values = domainHist.keySet();
//        for () {
//            cypher.put(domainHist)
//        }

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

    private static Map<Character, Integer> sortByValue(Map<Character, Integer> unsortedMap) {
        List<Map.Entry<Character, Integer>> list =
                new LinkedList<>(unsortedMap.entrySet());
        Collections.sort(list, (Map.Entry<Character, Integer> o1,
                               Map.Entry<Character, Integer> o2) -> (o2.getValue()).compareTo(o1.getValue()));
        Map<Character, Integer> sortedMap = new LinkedHashMap<>();
        for (Map.Entry<Character, Integer> entry : list) {
            sortedMap.put(entry.getKey(), entry.getValue());
        }
        return sortedMap;
    }

    @NotNull
    static Map<Character, Integer> createHist(@NotNull String text) {
        Map<Character, Integer> hist = new HashMap<>();
        for (int i = 0; i < CypherUtil.SYMBOLS.length(); i++) {
            hist.put(CypherUtil.SYMBOLS.charAt(i), 0);
        }
        for (int i = 0; i < text.length(); i++) {
            Character symbol = text.charAt(i);
            if (Character.isLetter(symbol)) {
                symbol = Character.toLowerCase(symbol);
                Integer value = hist.get(symbol);
                hist.put(symbol, value + 1);
            }
        }

//        Set<Character> set = hist.keySet();
//        System.out.println(set.toString());
//        for (Character c : set) {
//            System.out.print(hist.get(c));
//            System.out.print(" ");
//        }

        hist = sortByValue(hist);

//        set = hist.keySet();
//        System.out.println(set.toString());
//        for (Character c : set) {
//            System.out.print(hist.get(c));
//            System.out.print(" ");
//        }
        return hist;
    }
    public static void main(String[] args) {
        String text = "aabbbcb";
        Map<Character, Integer> map = createHist(text);
    }
}