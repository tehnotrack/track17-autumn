package ru.track.list;

import java.util.NoSuchElementException;

/**
 * Должен наследовать List
 * Односвязный список
 */
public class MyLinkedList extends List implements Stack, Queue {
    private Node tail;
    private Node head;

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

    public MyLinkedList(){
        super();
        head = null;
        tail = null;
    }

    @Override
    public void add(int item) {
        tail = new Node(tail, null, item);

        if (tail.prev != null){
            tail.prev.next = tail;
        }

        if (head == null) {
            head = tail;
        }

        size++;
    }

    private Node getNodeByIdx(int idx) {
        if (!check(idx)) {
            throw new NoSuchElementException();
        }

        Node currentNode;

        if (idx < size / 2) {
            currentNode = head;

            for (int i = 0; i < idx; i++) {
                currentNode = currentNode.next;
            }
        } else {
            currentNode = tail;

            for (int i = size - 1; i > idx; i--) {
                currentNode = currentNode.prev;
            }
        }

        return currentNode;
    }

    @Override
    public int remove(int idx) throws NoSuchElementException {
        Node nodeToRemove = getNodeByIdx(idx);

        if (nodeToRemove == head && nodeToRemove == tail) {
            head = null;
            tail = null;
        } else if (nodeToRemove == head) {
            head = head.next;
        } else if (nodeToRemove == tail) {
            tail = tail.prev;
        } else {
            nodeToRemove.prev.next = nodeToRemove.next;
            nodeToRemove.next.prev = nodeToRemove.prev;
        }

        size--;

        return nodeToRemove.val;
    }

    @Override
    public int get(int idx) throws NoSuchElementException {
        return getNodeByIdx(idx).val;
    }

    @Override
    public void push(int value) {
        add(value);
    }

    @Override
    public int pop() {
        return remove(size - 1);
    }

    @Override
    public void enqueue(int value) {
        add(value);
    }

    @Override
    public int dequeu() {
        return remove(0);
    }
}
