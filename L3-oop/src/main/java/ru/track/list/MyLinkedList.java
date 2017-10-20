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

    Node head;
    Node tail;
    int size;

    private static class Node {
        Node prev;
        Node next;
        int val;

        Node(Node prev, Node next, int val) {
            this.prev = prev;
            this.next = next;
            this.val = val;
        }

        Node(Node other) {
            this.prev = other.prev;
            this.next = other.next;
            this.val = other.val;
        }
    }

    @Override
    void add(int item) {
        Node temp = new Node(tail, null, item);
        if (size == 0) {
            head = tail = temp;
            size++;
            return;
        }

        tail.next = temp;
        tail = temp;
        size++;
    }

    @Override
    int remove(int idx) throws NoSuchElementException {
        if (idx < 0 || idx >= size) {
            throw new NoSuchElementException();
        }
        int result;

        if (idx == 0) {
            result = head.val;
            head = null;
            size--;
            return result;
        }

        if (idx == size - 1) {
            result = tail.val;
            tail = tail.prev;
            tail.next = null;
            size--;
            return result;
        }

        Node temp = new Node(head);
        for (int i = 0; i < idx - 1; i++, temp = temp.next) {
        }
        result = temp.next.val;
        temp.next = temp.next.next;
        temp.next.prev = temp;
        if (temp.prev != null) {
            temp.prev.next = temp;
        }
        else
        {
            head = temp;
        }

        size--;
        return 0;
    }

    @Override
    int get(int idx) throws NoSuchElementException {
        if (idx < 0 || idx >= size) {
            throw new NoSuchElementException();
        }

        Node temp = new Node(head);
        for (int i = 0; i < idx; i++, temp = temp.next) {
        }
        return temp.val;
    }

    @Override
    int size() {
        return size;
    }
}
