package ru.track.cypher;

import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.stream.Collectors;

public class Decoder {

    // Расстояние между A-Z -> a-z
    public static final int SYMBOL_DIST = 32;

    private Map<Character, Character> cypher;

    /**
     * Конструктор строит гистограммы открытого домена и зашифрованного домена
     * Сортирует буквы в соответствие с их частотой и создает обратный шифр Map<Character, Character>
     *
     * @param domain - текст по которому строим гистограмму языка
     */
    public Decoder(@NotNull String domain, @NotNull String encryptedDomain) {
        
        Map<Character, Integer> domainHist = createHist(domain);
        Map<Character, Integer> encryptedDomainHist = createHist(encryptedDomain);

        Map.Entry<Character, Character>[] encodedData = new Map.Entry[encryptedDomainHist.size()];
        encryptedDomainHist.entrySet().toArray(encodedData);
        Map.Entry<Character, Character>[] domainData = new Map.Entry[domainHist.size()];
        domainHist.entrySet().toArray(domainData);

        cypher = new LinkedHashMap<>();

        for (int counter = 0; counter < domainHist.size(); counter++) {
            cypher.put(encodedData[counter].getKey(), domainData[counter].getKey());
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
        Map<Character, Character> myCypher = this.getCypher();
        StringBuilder decodedData = new StringBuilder(encoded);

        for (int counter = 0; counter < encoded.length(); counter++) {
            char curChar = decodedData.charAt(counter);

            if (!Character.isLetter(curChar)) {
                continue;
            }

            decodedData.setCharAt(counter, myCypher.get(curChar));
        }
        return decodedData.toString();
    }

    /**
     * Считывает входной текст посимвольно, буквы сохраняет в мапу.
     * Большие буквы приводит к маленьким
     *
     * @param text - входной текст
     * @return - мапа с частотой вхождения каждой буквы (Ключ - буква в нижнем регистре)
     * Мапа отсортирована по частоте. При итерировании на первой позиции наиболее частая буква
     */
    @NotNull
    Map<Character, Integer> createHist(@NotNull String text) {
        Map<Character, Integer> myHistogramm = new HashMap<>(26);
        text = text.toLowerCase();


        for (int counter = 0; counter < text.length(); counter++) {
            char currentChar = text.charAt(counter);

            if (Character.isLetter(currentChar)) {
                int currentValue = myHistogramm.getOrDefault(currentChar, 0);
                myHistogramm.put(currentChar, ++currentValue);
            }
        }

        return myHistogramm.entrySet().stream()
                .sorted(Map.Entry.<Character, Integer>comparingByValue().reversed())
                .collect(Collectors.toMap(
                        Map.Entry::getKey, Map.Entry::getValue,
                        (oldValue, newValue) -> oldValue,
                        LinkedHashMap::new));
    }
}