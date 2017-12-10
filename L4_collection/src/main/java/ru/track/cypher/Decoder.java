package ru.track.cypher;

//import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;
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

        Iterator<Map.Entry<Character, Integer>> val = domainHist.entrySet().iterator();
        Iterator<Map.Entry<Character, Integer>> key = encryptedDomainHist.entrySet().iterator();

        cypher = new LinkedHashMap<>();

        while(key.hasNext() && val.hasNext()) {
            cypher.put(key.next().getKey(), val.next().getKey());
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
        encoded.toLowerCase();
        StringBuilder decoded = new StringBuilder();

        for (int i = 0; i < encoded.length(); ++i) {

            if(Character.isLetter(encoded.charAt(i))) {
                decoded.append(cypher.get(encoded.charAt(i)));
            } else decoded.append(encoded.charAt(i));
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
        Map<Character, Integer> hist = new LinkedHashMap<>();


        for (int i = 0; i < text.length(); i++) {

            if (Character.isLetter(text.charAt(i))) {
                if(hist.containsKey(text.charAt(i))) {
                    int quantity = hist.getOrDefault(text.charAt(i), 0);
                    hist.put(text.charAt(i),quantity +1);
                }else
                    hist.put(text.charAt(i),1);

            }
        }
        return hist.entrySet().stream()
                .sorted(Map.Entry.<Character,Integer>comparingByValue().reversed())
                .collect(Collectors.toMap(
                        Map.Entry::getKey, Map.Entry::getValue,
                        (oldValue, newValue) -> oldValue,
                        LinkedHashMap::new));
    }

}
