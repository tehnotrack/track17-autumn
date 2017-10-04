package ru.track.list;

import java.util.NoSuchElementException;

import org.junit.Assert;
import org.junit.Test;

/**
 *
 */
public class MyArrayListTest {

    @Test
    public void testResize1() {
        MyArrayList list = new MyArrayList(0);
        for (int i = 0; i < 1000; i++) {
            list.add(i);
        }

        Assert.assertTrue(list.size() == 1000);
    }

    @Test
    public void testResize2() {
        MyArrayList list = new MyArrayList(0);
        for (int i = 0; i < 100; i++) {
            list.add(i);
        }

        for (int i = 0; i < 100; i++) {
            list.remove(0);
        }
        Assert.assertTrue(list.size() == 0);

    }

    @Test(expected = NoSuchElementException.class)
    public void emptyList() throws Exception {
        List list = new MyArrayList();
        Assert.assertTrue(list.size() == 0);
        list.get(0);
    }

    @Test
    public void listAdd() throws Exception {
        List list = new MyArrayList();
        list.add(1);

        Assert.assertTrue(list.size() == 1);
    }

    @Test
    public void listAddRemove() throws Exception {
        List list = new MyArrayList();
        list.add(1);
        list.add(2);
        list.add(3);

        Assert.assertEquals(3, list.size());

        Assert.assertEquals(1, list.get(0));
        Assert.assertEquals(2, list.get(1));
        Assert.assertEquals(3, list.get(2));

        list.remove(1);
        Assert.assertEquals(3, list.get(1));
        Assert.assertEquals(1, list.get(0));

        list.remove(1);
        list.remove(0);

        Assert.assertTrue(list.size() == 0);
    }

    @Test
    public void listRemove() throws Exception {
        List list = new MyArrayList();
        list.add(1);
        list.remove(0);

        Assert.assertTrue(list.size() == 0);
    }
}
