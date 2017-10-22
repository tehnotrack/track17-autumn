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
     * @param domain - текст по которому строим гистограмму языка
     */
    public Decoder(@NotNull String domain, @NotNull String encryptedDomain) {
        Map<Character, Integer> domainHist = createHist(domain);
        Map<Character, Integer> encryptedDomainHist = createHist(encryptedDomain);
        Iterator<Character> iterDictionary = domainHist.keySet().iterator();
        Iterator<Character> iterEncrypted = encryptedDomainHist.keySet().iterator();

        cypher = new LinkedHashMap<>();
        for (int i = 0; iterDictionary.hasNext() && iterEncrypted.hasNext(); ++i) {
            cypher.put(iterEncrypted.next(), iterDictionary.next());
        }

        decode(encryptedDomain);


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
        for (int i = 0; i < encoded.length(); ++i) {
            if (Character.isLetter(encoded.charAt(i)))
                decoded.append(cypher.get(encoded.charAt(i)));
            else decoded.append(encoded.charAt(i));
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
        Map<Character, Integer> hist = new LinkedHashMap<>();
        text = text.toLowerCase();

        for (int i = 0; i < CypherUtil.SYMBOLS.length(); ++i ) {
            hist.put(CypherUtil.SYMBOLS.charAt(i), 0);
        }

        for (int i = 0; i < text.length(); ++i) {
            if (Character.isLetter(text.charAt(i))) {
                if (hist.keySet().contains(text.charAt(i))) {
                    hist.put(text.charAt(i), hist.get(text.charAt(i)) + 1);
                }
            }
        }

        List<Map.Entry<Character, Integer>> list = new ArrayList<>(hist.entrySet());
        list.sort((o1, o2) -> o2.getValue() - o1.getValue());
        Map<Character, Integer> sortedMap = new LinkedHashMap<>();
        for (Map.Entry<Character, Integer> entry : list) {
            sortedMap.put(entry.getKey(), entry.getValue());
        }
        return sortedMap;
    }
}
