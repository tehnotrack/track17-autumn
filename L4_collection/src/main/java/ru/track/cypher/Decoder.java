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

        Iterator<Map.Entry<Character, Integer>>
                domainIterator = domainHist.entrySet().iterator();
        Iterator<Map.Entry<Character, Integer>>
                encryptedDomainIterator = encryptedDomainHist.entrySet().iterator();

        cypher = new LinkedHashMap<>();

        while ((domainIterator.hasNext()) && (encryptedDomainIterator.hasNext())){
            cypher.put(encryptedDomainIterator.next().getKey(), domainIterator.next().getKey());
        }

        return;
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
        char[] charArray = encoded.toLowerCase().toCharArray();

        for (int i = 0; i < encoded.length(); i++){
            charArray[i] = cypher.getOrDefault(charArray[i], charArray[i]);
        }

        return new String(charArray);
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

        HashMap <Character, Integer> tmpHist = new HashMap<>();

        char[] charArray = text.toLowerCase().toCharArray();

        for (char c : charArray){
            if ((c >= 'a') && (c <= 'z')){
                if (tmpHist.containsKey(c)){
                    tmpHist.put(c, tmpHist.get(c) + 1);
                }else {
                    tmpHist.put(c, 1);
                }
            }
        }

        List<Map.Entry<Character, Integer>> mapInList = new ArrayList<>(tmpHist.entrySet());

        mapInList.sort(new Comparator<Map.Entry<Character, Integer>>() {
            @Override
            public int compare(Map.Entry<Character, Integer> o1, Map.Entry<Character, Integer> o2) {

                return o2.getValue() -  o1.getValue();
            }
        });

        Map<Character, Integer> hist = new LinkedHashMap<>();

        for(Map.Entry<Character, Integer> entry : mapInList){
            hist.put(entry.getKey(), entry.getValue());
        }

        return hist;
    }

}
