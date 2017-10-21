package ru.track.cypher;

import java.util.LinkedHashMap;
import java.util.*;
import java.io.*;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

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
        Iterator<Character> encryptedDomainIt = encryptedDomainHist.keySet().iterator();
        while(encryptedDomainIt.hasNext() && domainIt.hasNext()) {
            cypher.put(encryptedDomainIt.next(), domainIt.next());
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

        StringBuilder sb = new StringBuilder();

        for ( int i = 0 ; i < encoded.length(); i++){
            char tmpChar = Character.toLowerCase(encoded.charAt(i));
            if (tmpChar >= 'a' && tmpChar <= 'z')
            {
                sb.append(cypher.get(tmpChar));
            }
            else sb.append(tmpChar);

        }

        return sb.toString();
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

        Map<Character,Integer> histMap = new HashMap<>();

        for ( int i = 0 ; i < 26; i++){

            histMap.put((char) (Character.valueOf('a') + i) ,0);

        }

        for( int i = 0; i < text.length(); i++){

            char tmpChar = Character.toLowerCase(text.charAt(i));
            if ((tmpChar >= 'a') && (tmpChar <= 'z'))
            {
                histMap.put(tmpChar,histMap.get(tmpChar)+1);
            }

        }

        class MyComparator implements Comparator {

            Map map;

            public MyComparator(Map map) {
                this.map = map;
            }

            public int compare(Object o1, Object o2) {

                return ((Integer) map.get(o2)).compareTo((Integer) map.get(o1));

            }
        }

        MyComparator comp = new MyComparator(histMap);

        Map<Character, Integer> sortedMap = new TreeMap<>(comp);
        sortedMap.putAll(histMap);

        return sortedMap;
    }


}






