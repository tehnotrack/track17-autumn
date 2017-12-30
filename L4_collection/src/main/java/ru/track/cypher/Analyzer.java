package ru.track.cypher;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 *
 */
public class Analyzer {

    public static final String SYMBOLS = "abcdefghijklmnopqrstuvwxyz";

    public static Map<Character, Integer> printHist(@NotNull String text, @Nullable Predicate<Character> characterPredicate) {
        Map<Character, Integer> hist = new HashMap<>();

        for (int i = 0; i < text.length(); i++) {
            char ch = text.charAt(i);
            if (characterPredicate != null && characterPredicate.test(ch)) {
                Integer count = hist.get(ch);
                if (count == null) {
                    hist.put(ch, 1);
                } else {
                    hist.put(ch, count + 1);
                }
            }
        }

        //System.out.println(hist);
        return hist;
    }

    public static void listToSet() {
        List<String> strs = Arrays.asList("AA", "BB", "CCC", "AA");

        Collections.sort(strs);
        System.out.println(strs);

    }


    public static void main(String[] args) {

        List<Character> letters = new ArrayList<>();
        for (int i = 0; i < SYMBOLS.length(); i++) {
            letters.add(SYMBOLS.charAt(i));
        }

        System.out.println(letters);

        Collections.shuffle(letters);
        System.out.println(letters);


        listToSet();






//        Map<Character, Integer> hist = printHist("AaBb BBAAa", Character::isLetter);
        Map<Character, Integer> hist = printHist("abbcddddef", Character::isLetter);

        Collection<Integer> values = hist.values();

        // считаем сумму
        int sum = 0;
        for (Integer val : values) {
            sum += val;
        }

        // int sum = values.stream().mapToInt(v -> v).sum();


        for (Map.Entry<Character, Integer> entry : hist.entrySet()) {
            int val = entry.getValue();
            entry.setValue((val * 100 / sum));
        }

        //System.out.println(hist);

        List<Integer> listValues = new ArrayList<>(values);
        listValues.sort((o1, o2) -> {
            System.out.println()


            ;
            return o2 - o1;
        });
        //System.out.printf("before:\t%s\nafter:\t%s\n", values, listValues);



        Map<Character, Integer> sortedHist = new LinkedHashMap<>();
        List<Map.Entry<Character, Integer>> entries = new ArrayList<>(hist.entrySet());
//        entries.sort(new MyComparator())

    }

    static class MyComparator implements Comparator<Integer> {
        @Override
        public int compare(Integer o1, Integer o2) {
            if (o1 > o2) {
                return 1;
            } else if (o2 > o1) {
                return -1;
            } else {
                return 0;
            }
        }
    }
}
