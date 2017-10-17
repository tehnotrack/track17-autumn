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
        Iterator<Character> domainIt = domainHist.keySet().iterator();
        Iterator<Character> encryptedIt = encryptedDomainHist.keySet().iterator();

        while (domainIt.hasNext() && encryptedIt.hasNext())
        {
            cypher.put(encryptedIt.next(), domainIt.next());
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
    public String decode(@NotNull String encoded)
    {
        StringBuffer decodedText = new StringBuffer();

        for (int i = 0; i < encoded.length(); i++) {
            if (Character.isLetter(encoded.charAt(i))) {
                decodedText.append(cypher.get(encoded.charAt(i)));
            }
            else {
                decodedText.append(encoded.charAt(i));
            }
    }

        return decodedText.toString();
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

        // building hist
        for (int i = 0; i < text.length(); i++) {
            char ch = Character.toLowerCase(text.charAt(i));
            if (Character.isLetter(ch)) {
                Integer count = hist.get(ch);
                if (count == null) {
                    hist.put(ch, 1);
                }
                else {
                    hist.put(ch, count + 1);
                }
            }
        }

        // sort by frequency
        List<Map.Entry<Character, Integer>> list = new LinkedList<>(hist.entrySet());
        list.sort((Map.Entry<Character, Integer> o1, Map.Entry<Character, Integer> o2) -> {
            if (o1.getValue() < o2.getValue()) return 1;
            else if (o1.getValue() > o2.getValue()) return -1;
            else return 0;
        });

        Map<Character, Integer> result = new LinkedHashMap<>();
        for (Map.Entry<Character, Integer> entry : list) {
            result.put(entry.getKey(), entry.getValue());
        }
        
        return result;
    }
}
