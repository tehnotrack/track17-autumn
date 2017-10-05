package ru.track.list;

import java.util.NoSuchElementException;

/**
 * Должен наследовать List
 * Односвязный список
 */
public class MyLinkedList extends List {

    private int sizeOfList = 0;
    private Node current, head;

    public MyLinkedList() {
        head = current = new Node(null, null, 0);
    }

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
        current.next = new Node(current, null, item);
        current = current.next;
        sizeOfList++;
    }

    @Override
    int remove(int idx) throws NoSuchElementException {
        if(idx < 0 || idx >= sizeOfList)
            throw new NoSuchElementException();
        Node idxNode = head;
        for(int i = 0; i <= idx; i++){
            idxNode = idxNode.next;
        }
        if(idxNode.next != null) {
            idxNode.next.prev = idxNode.prev;
            idxNode.prev.next = idxNode.next;
        }
        else {
            current = current.prev;
        }
        sizeOfList--;
        return 0;
    }

    @Override
    int get(int idx) throws NoSuchElementException {
        if(idx < 0 || idx >= sizeOfList)
            throw new NoSuchElementException();
        Node idxNode = head;
        for(int i = 0; i <= idx; i++){
            idxNode = idxNode.next;
        }
        return idxNode.val;
    }

    @Override
    int size() {
        return sizeOfList;
    }
}
