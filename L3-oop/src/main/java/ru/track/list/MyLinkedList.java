package ru.track.list;

import java.util.NoSuchElementException;

/**
 * Должен наследовать List
 * Односвязный список
 */
public class MyLinkedList extends List {
    Node beg;
    Node end;
    int size = 0;

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

        Node(int val) {
            this.prev = null;
            this.next = null;
            this.val = val;
        }
    }

    @Override
    void add(int item) {
        Node n = new Node(item);
        if (size == 0) {
            beg = n;
            end = n;
        } else {
            end.next = n;
            n.prev = end;
            end = n;
        }
        size++;
    }

    @Override
    int remove(int idx) throws NoSuchElementException {
        if ((idx <= size) & (idx >= 0)) {
            Node n = beg;

            for (int i = 0; i < idx; i++) {
                n = n.next;
            }
            if (idx == 0) {
                n.next = beg;
                n.prev = null;
            }
            if ((idx > 0) & (idx < size - 1)) {
                n.prev.next = n.next;
                n.next.prev = n.prev;
            } else {
                n.prev = end;
                n.next = null;
            }
            size--;
            return n.val;
        } else throw new NoSuchElementException();
    }

    @Override
    int get(int idx) throws NoSuchElementException {
        if ((idx < size) & (idx >= 0)) {
            Node n = beg;
            for (int i = 0; i < idx; i++) n = n.next;
            return n.val;
        } else throw new NoSuchElementException();
    }

    @Override
    int size() {
        return size;
    }
}
