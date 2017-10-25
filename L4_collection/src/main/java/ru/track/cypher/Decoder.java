package ru.track.cypher;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

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

        cypher = new LinkedHashMap<Character, Character>();
        fillCypher(domainHist, encryptedDomainHist);
    }

    private void fillCypher(Map<Character, Integer> domainHist, Map<Character, Integer> encryptedDomainHist) {
        ArrayList<Character> domainChars = new ArrayList<>();
        ArrayList<Character> encryptedDomainChars = new ArrayList<>();

        domainHist.forEach((k, v) -> domainChars.add(k));
        encryptedDomainHist.forEach((k, v) -> encryptedDomainChars.add(k));

        for (int i = 0; i < domainChars.size(); i++) cypher.put(encryptedDomainChars.get(i), domainChars.get(i));
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
        StringBuilder builder = new StringBuilder();

        for (int i=0; i < encoded.length(); i++) {
            Character oldSymbol = Character.toLowerCase(encoded.charAt(i));

            if (cypher.containsKey(oldSymbol)) builder.append(cypher.get(oldSymbol).charValue());
            else builder.append(encoded.charAt(i));
        }

        return builder.toString();
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
        Map<Character, Integer> resultMap = new LinkedHashMap<>();

        for (int i = 0; i < text.length(); i++) {
            Character symbol = Character.toLowerCase(text.charAt(i));

            if (CypherUtil.SYMBOLS.indexOf(symbol) >= 0) {
                Integer count = resultMap.get(symbol);

                if (count == null) resultMap.put(symbol, 1);
                else resultMap.put(symbol, count + 1);
            }
        }

        return sortMapByValue(resultMap);
    }

    private Map<Character, Integer> sortMapByValue(Map<Character, Integer> map) {
        Map<Character, Integer> resultMap = new LinkedHashMap<>();

        map.entrySet()
                .stream()
                .sorted(Map.Entry.<Character, Integer>comparingByValue().reversed())
                .forEach(x -> resultMap.put(x.getKey(), x.getValue()));

        return resultMap;
    }

}
