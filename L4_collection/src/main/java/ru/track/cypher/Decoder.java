package ru.track.cypher;

import java.security.KeyStore;
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
        Iterator<Character> domainIter = domainHist.keySet().iterator();
        Iterator<Character> encryptedDomainIter = encryptedDomainHist.keySet().iterator();
        while(encryptedDomainIter.hasNext()) {
            cypher.put(encryptedDomainIter.next(), domainIter.next());
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
        char local;
        for (int i = 0; i < encoded.length(); i++) {
            local = Character.toLowerCase(encoded.charAt(i));
            if (local > 'z' || local < 'a') {
                decoded.append(encoded.charAt(i));
                continue;
            }
            decoded.append(cypher.get(local));
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
        LinkedHashMap<Character, Integer> hist = new LinkedHashMap<>();
        char local;
        for (int i = 0; i < 26; i++) {
            hist.put((char) (i + Character.valueOf('a')), 0);
        }
        for (int i = 0; i < text.length(); i++) {
            local = Character.toLowerCase(text.charAt(i));
            if (local > 'z' || local < 'a') {
                continue;
            }
            hist.put(local, hist.get(local) + 1);
        }
        ArrayList<Map.Entry<Character, Integer>> list = new ArrayList<>(hist.entrySet());
        list.sort(Comparator.comparingInt((e) -> -e.getValue()));
        hist = new LinkedHashMap<>();
        for (Map.Entry<Character, Integer> e: list) {
            hist.put(e.getKey(), e.getValue());
        }
        return hist;
    }

    public static void main(String... args) {
        Map<Character, Integer> hist = new Decoder("a", "b").createHist("sdfsdf");
        hist.forEach((a, b)->System.out.println(a + ": " + b + " "));
    }

}
