package ru.track.cypher;

import java.io.File;
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
        cypher = new HashMap<>();
        List<Map.Entry<Character, Integer>> entries = new ArrayList<>(domainHist.entrySet());
        List<Map.Entry<Character, Integer>> encryptedEntries = new ArrayList<>(encryptedDomainHist.entrySet());
        Iterator<Map.Entry<Character, Integer>> iter = entries.iterator();
        for(Map.Entry<Character, Integer> entry : encryptedEntries){
                cypher.put(entry.getKey(), iter.next().getKey());
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
        char c;
        for(int i = 0; i < encoded.length(); i++){
            c = Character.toLowerCase(encoded.charAt(i));
            if(cypher.containsKey(c))
                decoded.append(cypher.get(c));
            else decoded.append(c);
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
        Map<Character, Integer> hist =  new HashMap<>();
        char c;
        for(int i = 0; i < text.length(); i++) {
            c = text.charAt(i);
            if (Character.isLetter(c)) {
                if(hist.containsKey(c))
                    hist.put(c, hist.get(c) + 1);
                else hist.put(c, 1);
            }
        }
        List<Map.Entry<Character, Integer>> entries = new ArrayList<>(hist.entrySet());
        entries.sort(new MyComparator());
        Map<Character, Integer> sortedHist = new LinkedHashMap<>();
        for(Map.Entry<Character, Integer> entry : entries)
              sortedHist.put(entry.getKey(), entry.getValue());
        return sortedHist;
    }

    static class MyComparator implements Comparator<Map.Entry<Character, Integer>> {
        @Override
        public int compare(Map.Entry<Character, Integer> x, Map.Entry<Character, Integer> y) {
            return y.getValue() - x.getValue();
        }
    }
}
