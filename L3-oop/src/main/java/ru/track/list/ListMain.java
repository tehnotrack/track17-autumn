package ru.track.list;

/**
 *
 */
public class ListMain {

    public static void main(String[] args) {
        MyLinkedList list = new MyLinkedList();
        list.add(10);
        list.add(20);
        list.add(30);
        list.add(1000);
        list.remove(2);
        list.remove(2);
        list.remove(0);
        list.remove(1);
        list.printList();
    }
}
