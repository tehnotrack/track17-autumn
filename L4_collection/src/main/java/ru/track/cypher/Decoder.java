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

        List<Character> domainHistKeys = new LinkedList<>(domainHist.keySet());
        List<Character> encryptedDomainHistKeys= new LinkedList<>(encryptedDomainHist.keySet());

        for(int i=0; i<encryptedDomainHistKeys.size(); ++i){
            cypher.put(encryptedDomainHistKeys.get(i),domainHistKeys.get(i));
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

        if (encoded.length() == 0)
            return encoded;

        Character ch;

        StringBuilder decoded = new StringBuilder();

        for (int i = 0; i < encoded.length(); i++) {
            ch = cypher.get(Character.toLowerCase(encoded.charAt(i)));
            if (ch == null)
                decoded.append(encoded.charAt(i));
            else
                decoded.append(ch.charValue());
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
        Map<Character, Integer> hist = new HashMap<>();

        for (int i = 0; i < text.length(); i++) {
            char ch = Character.toLowerCase(text.charAt(i));

            Integer count = hist.get(ch);
            if (count == null) {
                hist.put(ch, 1);
            } else {
                hist.put(ch, count + 1);
            }
        }

        List<Map.Entry<Character, Integer>> list = new ArrayList<>(hist.entrySet());
        Collections.sort(list, new Comparator<Map.Entry<Character, Integer>>() {
            @Override
            public int compare(Map.Entry<Character, Integer> a, Map.Entry<Character, Integer> b) {
                return a.getValue() - b.getValue();
            }
        });

        hist.clear();

        for (Map.Entry<Character, Integer> entry : list) {
            hist.put(entry.getKey(), entry.getValue());
        }

        return hist;
    }

}
