package ru.track.list;

import java.util.NoSuchElementException;

/**
 * Должен наследовать List
 * Односвязный список
 */
public class MyLinkedList extends List {
    private Node first;
    private int size = 0;

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

    @Override
    void add(int item) {
        if (first == null) {
            first = new Node(null, null, item);
            size++;
            return;
        }
        Node tmp = first;
        while (tmp.next != null) {
            tmp = tmp.next;
        }
        tmp.next = new Node(tmp, null, item);
        size++;
        return;
    }

    @Override
    int remove(int idx) throws NoSuchElementException {
        if (idx < 0 || size == 0 || idx > size)
            throw new NoSuchElementException();
        Node tmp = first;
        while (idx != 0 && tmp.next != null) {
            idx--;
            tmp = tmp.next;
        }
        if (idx == 0) {
            int result = tmp.val;
            if (tmp.prev == null) {
                first = null;
                size--;
                return result;
            }
            if (tmp.next == null) {
                tmp.prev.next = null;
                size--;
                return result;
            }
            tmp.prev.next = tmp.next;
            size--;
            return result;
        }
        throw new NoSuchElementException();
    }

    @Override
    int get(int idx) throws NoSuchElementException {
        if (idx < 0 || size == 0 || idx > size)
            throw new NoSuchElementException();
        Node tmp = first;
        while (idx != 0 && tmp.next != null) {
            idx--;
            tmp = tmp.next;
        }
        if (idx == 0)
            return tmp.val;
        throw new NoSuchElementException();
    }

    @Override
    int size() {
        return size;
    }
}
