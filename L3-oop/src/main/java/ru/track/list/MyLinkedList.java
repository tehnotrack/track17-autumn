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

    private Node head;
    private Node tail;

    private Node getNode(int idx) {
        Node tempNode = head;

        for (int i = 0; i < idx; i++)
            tempNode = tempNode.next;

        return tempNode;
    }

    @Override
    void add(int item) {
        Node prevTail = tail;
        tail = new Node(prevTail, null, item);
        size++;

        if (size == 1)
            head = tail;
        else
            prevTail.next = tail;
    }

    @Override
    int remove(int idx) throws NoSuchElementException {
        checkID(idx);

        Node removableNode = getNode(idx);
        int result = removableNode.val;

        Node prevNode = removableNode.prev;
        Node nextNode = removableNode.next;

        size--;

        if (prevNode != null)
            prevNode.next = nextNode;
        else {
            head = nextNode;
        }

        if (nextNode != null)
            nextNode.prev = prevNode;
        else {
            tail = prevNode;
        }

        return result;
    }

    @Override
    int get(int idx) throws NoSuchElementException {
        checkID(idx);

        Node varNode = getNode(idx);

        return varNode.val;
    }
}
