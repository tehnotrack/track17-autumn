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

    private Node head = null;
    private Node tail = null;
    private int size = 0;

    @Override
    void add(int item) {
        Node newNode;

        if (this.size == 0) {
            newNode = new Node(null, null, item);
            this.head = newNode;
            this.tail = newNode;
        } else {
            newNode = new Node(this.tail, null, item);
            this.tail.next = newNode;
            this.tail = newNode;
        }

        ++this.size;
    }

    @Override
    int remove(int idx) throws NoSuchElementException {
        if (checkIndex(idx)) {
            throw new NoSuchElementException();
        }

        Node node = this.head;

        for (int i = 0; i < idx; ++i) {
            node = node.next;

            if (node == null) {
                throw new NoSuchElementException();
            }
        }

        int removedElement = node.val;

        if (node.prev != null) {
            node.prev.next = node.next;
        } else {
            this.head = node.next;
        }

        if (node.next != null) {
            node.next.prev = node.prev;
        } else {
            this.tail = node.prev;
        }

        --this.size;

        return removedElement;
    }

    @Override
    int get(int idx) throws NoSuchElementException {
        if (checkIndex(idx)) {
            throw new NoSuchElementException();
        }

        Node node = this.head;

        for (int i = 0; i < idx; ++i) {
            node = node.next;

            if (node == null) {
                throw new NoSuchElementException();
            }
        }

        return node.val;
    }

    @Override
    int size() {
        return this.size;
    }

    private boolean checkIndex(int idx) {
        return idx < 0 || idx >= this.size;
    }
}
