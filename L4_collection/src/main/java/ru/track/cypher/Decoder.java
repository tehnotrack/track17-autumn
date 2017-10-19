package ru.track.cypher;

import java.util.*;

import org.jetbrains.annotations.NotNull;

public class Decoder {

    private Map<Character, Character> cypher;

    /**
     * Конструктор строит гистограммы открытого домена и зашифрованного домена
     * Сортирует буквы в соответствие с их частотой и создает обратный шифр Map<Character, Character>
     *
     * @param domain - текст по кторому строим гистограмму языка
     */
    public Decoder(@NotNull String domain, @NotNull String encryptedDomain) {
        Map<Character, Integer> domainHist = createHist(domain);
        List<Character> symbolsList = new ArrayList<>(domainHist.keySet());
        Map<Character, Integer> encryptedDomainHist = createHist(encryptedDomain);
        List<Character> encryptedSymbolsList = new ArrayList<>(encryptedDomainHist.keySet());
        cypher = new LinkedHashMap<>();
        for (int i = 0; i < symbolsList.size(); ++i) {
            cypher.put(encryptedSymbolsList.get(i), symbolsList.get(i));
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
        StringBuilder decodedText = new StringBuilder();
        for (char encodedSymbol : encoded.toCharArray()) {
            Character decodedSymbol = cypher.get(encodedSymbol);
            if (decodedSymbol != null) {
                decodedText.append(decodedSymbol);
            } else {
                decodedText.append(encodedSymbol);
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
    LinkedHashMap<Character, Integer> createHist(@NotNull String text) {
        Map<Character, Integer> histogram = new HashMap<>();
        for (int i = 0; i < text.length(); ++i) {
            Character symbol = Character.toLowerCase(text.charAt(i));
            if (Character.isLetter(symbol)) {
                Integer frequency = histogram.get(symbol);
                if (frequency != null) {
                    histogram.put(symbol, frequency + 1);
                } else {
                    histogram.put(symbol, 1);
                }
            }
        }
        List<Character> symbolsList = new LinkedList<>(histogram.keySet());
        symbolsList.sort(Comparator.comparing(histogram::get));
        LinkedHashMap<Character, Integer> sortedHistogram = new LinkedHashMap<>();
        for (int i = symbolsList.size() - 1; i >= 0; --i) {
            Character symbol = symbolsList.get(i);
            sortedHistogram.put(symbol, histogram.get(symbol));
        }
        return sortedHistogram;
    }

}
