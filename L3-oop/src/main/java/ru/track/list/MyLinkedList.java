package ru.track.list;

import java.util.NoSuchElementException;

/**
 * Должен наследовать List
 * Односвязный список
 */
public class MyLinkedList extends List {
    private Node head;
    private Node tail;
    private int size;

    public MyLinkedList() {
        this.head = null;
        this.tail = null;
        this.size = 0;
    }

    /**
     * private - используется для сокрытия этого класса от других.
     * Класс доступен только изнутри того, где он объявлен
     * <p>
     * static - позволяет использовать Node без создания экземпляра внешнего класса
     */
    private static class Node {
        Node next;
        int val;

        Node(Node next, int val) {
            this.next = next;
            this.val = val;
        }

        Node(int val) {
            this(null, val);
        }

        Node getNext() {
            return this.next;
        }

        void setNext(Node node) {
            this.next = node;
        }

        int getData() {
            return this.val;
        }

    }

    @Override
    public void add(int item) {
        Node newNode = new Node(item);
        if (size == 0) {
            head = newNode;
            tail = head;
        } else {
            (tail).setNext(newNode);
            tail = newNode;
        }
        size++;
    }

    @Override
    public int remove(int idx) throws NoSuchElementException {
        if (head == null) {
            return 0;
        } else if (idx == 0) {
            --size;
            return head.getData();
        } else {
            Node n = head;
            for (int i = 0; i < idx - 1; i++) {
                n = n.next;
            }
            n.next = n.next.next;
            --size;
            return head.getData();
        }
    }

    @Override
    public int get(int idx) throws NoSuchElementException {
        if (idx < 0 || idx >= size) {
            throw new NoSuchElementException("invalid index");
        }
        Node currentNode = this.head;
        for (int i = 0; i < idx; i++) {
            currentNode = currentNode.getNext();
        }
        return currentNode.getData();
    }

    @Override
    public int size() {
        return size;
    }
}