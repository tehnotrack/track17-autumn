package ru.track.list;

import java.util.NoSuchElementException;

/**
 * Должен наследовать List
 * Односвязный список
 */
public class MyLinkedList extends List {

    /**
     * private - используется для сокрытия этого класса от других.
     * Класс доступен только изнутри того, где он объявлен
     * <p>
     * static - позволяет использовать Node без создания экземпляра внешнего класса
     */

    private static class Node {
        Node prev;
        Node next;
        int val;

        Node(Node prev, Node next, int val) {
            this.prev = prev;
            this.next = next;
            this.val = val;
        }
    }

    private Node root;
    private Node heap;

    public MyLinkedList() {

        this.root = null;
        this.heap = null;

        size = 0;
    }

    @Override
    void add(int item) {

        if (this.root == null) {
            this.root = new Node(null, null, item);
            this.heap = this.root;
        } else {
            this.heap.next = new Node(this.heap, null, item);
            this.heap = this.heap.next;
        }

        size++;
    }

    @Override
    int remove(int idx) throws NoSuchElementException {

        if (idx >= size) {
            throw new NoSuchElementException();
        }

        Node temp = root;

        for (int i = 0; i < idx; i++) {
            temp = temp.next;
        }

        if (temp.prev == null) {
            root = null;
        } else {
            temp.prev.next = temp.next;
        }

        size--;

        return temp.val;
    }

    @Override
    int get(int idx) throws NoSuchElementException {

        if (idx >= size) {
            throw new NoSuchElementException();
        }

        Node temp = root;

        for (int i = 0; i < idx; i++) {
            temp = temp.next;
        }

        return temp.val;
    }

    @Override
    int size() {
        return size;
    }
}
