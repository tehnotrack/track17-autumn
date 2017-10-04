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

    Node head;
    Node tail;
    int size;

    @Override
    void add(int item) {
        if (size == 0) {
            tail = head = new Node(null, null, item);
            size = 1;
        } else {
            tail.next = new Node(tail, null, item);
            tail = tail.next;
            size++;
        }
    }

    @Override
    int remove(int idx) throws NoSuchElementException {
        if (idx < 0 || idx >= size) {
            throw new NoSuchElementException();
        }
        Node current = head;
        for (int i = 0; i < idx; ++i) {
            current = current.next;
        }
        if (current.prev != null) {
            current.prev.next = current.next;
        }
        if (current.next != null) {
            current.next.prev = current.prev;
        }
        size--;
        if (size == 0) {
            tail = head = null;
        }
        return current.val;
    }

    @Override
    int get(int idx) throws NoSuchElementException {
        if (idx < 0 || idx >= size) {
            throw new NoSuchElementException();
        }
        Node current = head;
        for (int i = 0; i < idx; ++i) {
            current = current.next;
        }
        return current.val;
    }

    @Override
    int size() {
        return size;
    }
}
