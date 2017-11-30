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

        Iterator iterDomain = domainHist.keySet().iterator();
        Iterator iterEncrypted = encryptedDomainHist.keySet().iterator();
        while(iterDomain.hasNext() && iterEncrypted.hasNext()){
            cypher.put((Character) iterEncrypted.next(), (Character)iterDomain.next());
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
        Encoder enc = new Encoder();
        return enc.encode(cypher, encoded);
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
        Map<Character, Integer> hist =  new HashMap<>();
        text = text.toLowerCase();
        for(int i = 0; i < text.length(); i++){
            Character Char = text.charAt(i);
            if(Character.isLetter(Char)) {
                if (hist.containsKey(Char)) {
                    hist.put(Char, hist.get(Char) + 1);
                } else {
                    hist.put(Char, 1);
                }
            }
        }

        ArrayList<Map.Entry<Character, Integer>> list = new ArrayList<>(hist.entrySet());
        Collections.sort(list, (e1, e2) -> {return (int)e2.getValue() - (int)e1.getValue();});

        hist =  new LinkedHashMap<>();
        for(Map.Entry entry: list){
            hist.put((Character)entry.getKey(), (Integer)entry.getValue());
        }

        return hist;
    }

}
