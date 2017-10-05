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
    Node tail;
    Node head;
    private static class Node {
        Node prev;
        Node next;
        int val;

        Node(Node prev, Node next, int val) {
            this(prev, val);
            this.next = next;
        }
        Node(int val) {
            this.val = val;
        }
        Node(Node prev, int val) {
            this(val);
            this.prev = prev;
        }
    }

    @Override
    void add(int item) {
        if (elem_count == 0) {
            head = new Node(item);
            tail = head;
            elem_count++;
            return;
        }
        tail.next = new Node(tail, item);
        tail = tail.next;
        elem_count++;
    }

    @Override
    int remove(int idx) throws NoSuchElementException {
        if (elem_count <= idx) {
            throw new NoSuchElementException("Removing from empty list!");
        }
        int res = 0;

        if (idx == 0) {
            res = head.val;
            head = head.next;
        } else if (idx == elem_count - 1) {
            res = tail.val;
            tail = tail.prev;
        } else {
            int ind = 0;
            for (Node iter = head; iter != tail; iter = iter.next) {
                if (ind == idx) {
                    res = iter.val;
                    iter.prev.next = iter.next;
                    iter.next.prev = iter.prev;
                    break;
                }
                ind++;
            }
        }
        elem_count--;
        return res;
    }

    @Override
    int get(int idx) throws NoSuchElementException {
        if (elem_count <= idx) {
            throw new NoSuchElementException("Getting an element with unexisting index");
        }
        int ind = 0;
        for (Node iter = head; iter != tail.next; iter = iter.next) {
            if (ind == idx) {
                return  iter.val;
            }
            ind++;
        }
        return 0;
    }

    @Override
    int size() {
        return elem_count;
    }
}
