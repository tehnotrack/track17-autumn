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
        List<Character> domainHistList = new ArrayList<Character>();
        List<Character> encryptedDomainHistList = new ArrayList<Character>();
        domainHistList.addAll(domainHist.keySet());
        encryptedDomainHistList.addAll( encryptedDomainHist.keySet());
        for(int i=0; i<encryptedDomainHistList.size(); ++i){
            cypher.put(encryptedDomainHistList.get(i),domainHistList.get(i));
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
        Map<Character, Character> cypher = getCypher();
        StringBuilder text = new StringBuilder();
        encoded=encoded.toLowerCase();
        for (int i=0; i<encoded.length(); ++i){
            if(cypher.containsKey(encoded.charAt(i))) {
                text.append(cypher.get(encoded.charAt(i)));
            }
            else {
                text.append(encoded.charAt(i));
            }
        }
//        System.out.println("text: "+text.toString());
        return text.toString();
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

    public static <K, V extends Comparable<? super V>> Map<K, V>
    sortByValue(Map<K, V> map) {
        List<Map.Entry<K, V>> list = new LinkedList<Map.Entry<K, V>>(map.entrySet());
        Collections.sort( list, new Comparator<Map.Entry<K, V>>() {
            public int compare(Map.Entry<K, V> o1, Map.Entry<K, V> o2) {
                return (o1.getValue()).compareTo( o2.getValue() );
            }
        });
        Collections.reverse(list);
        Map<K, V> result = new LinkedHashMap<K, V>();
        for (Map.Entry<K, V> entry : list) {
            result.put(entry.getKey(), entry.getValue());
        }
        return result;
    }

    @NotNull
    Map<Character, Integer> createHist(@NotNull String text) {
        Map<Character, Integer> hist = new HashMap<>();
        String newText = text.toLowerCase();
        for(int i=0; i<text.length(); ++i){
            if(Character.isLetter(newText.charAt(i))) {
                if (hist.containsKey(newText.charAt(i))) {
                    hist.replace(newText.charAt(i), hist.get(newText.charAt(i)) + 1);
                } else {
                    hist.put(text.toLowerCase().charAt(i), 1);
                }
            }
        }

        return sortByValue(hist);
    }

}
