package ru.track.list;

/**
 *
 */
public class ListMain {

    public static void main(String[] args) {
        List list = new MyArrayList();
        list.add(1);
        list.add(2);
        list.add(3);

        System.out.println(list.get(0));
    }
}
