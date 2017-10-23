package ru.track.cypher;

import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

/**
 *
 */
public class CollectionsTest {


    @Test
    public void collections() {
        List<Integer> list = Arrays.asList(3, 2, 1, 4);

        Collections.sort(list);
        List<Integer> expected = Arrays.asList(1, 2, 3, 4);
        Assert.assertEquals(expected, list);

        Comparator<Integer> reverseIntCmp = new Comparator<Integer>() {
            @Override
            public int compare(Integer o1, Integer o2) {
                if (o2 > o1) return 1;
                else if (o2 < o1) return -1;
                return 0;
            }
        };

        expected = Arrays.asList(4, 3, 2, 1);
        Collections.sort(list, reverseIntCmp);
        Assert.assertEquals(expected, list);


        List<String> strs = Arrays.asList("AAA", "B", "CC");
        Collections.sort(strs, new Comparator<String>() {
            @Override
            public int compare(String o1, String o2) {
                if (o2.length() > o1.length()) return 1;
                else if (o2.length() < o1.length()) return -1;
                return 0;
            }
        });
        List<String> expectedStr = Arrays.asList("AAA", "CC", "B");
        Assert.assertEquals(expectedStr, strs);

        strs.sort(Comparator.comparing(String::length).reversed());
        System.out.println(strs);
    }

}
