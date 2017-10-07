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


    int size = 0;
    Node begin = null;
    Node end = null;

    @Override
    void add(int item) {
        if(begin == null && end == null) { //Если список был пустой
            begin = new Node(null, null, item);
            end = begin;
        }
        else {
            Node new_node = new Node(null, null, item);
            end.next = new_node;
            new_node.prev = end;
            end = end.next;
        }
        size++;
    }

    void printList() {
        Node current = begin;
        while(current != null) {
            System.out.print(current.val + " ");
            current = current.next;
        }
    }

    @Override
    int remove(int idx) throws NoSuchElementException {
        if(idx >= size || idx < 0) {
            throw new NoSuchElementException();
        }
        int removed_element;
        //Удаление первого элемента
        if(idx == 0) {
            removed_element = begin.val;
            if(size == 1) {
                begin = null;
                end = null;
            }
            else {
                begin.next.prev = null;
                begin = begin.next;
            }
        }
        else if(idx == size - 1) {
            removed_element = end.val;
            if(size == 1) {
                begin = null;
                end = null;
            }
            else {
                end.prev.next = null;
                end = end.prev;
            }
        }
        else {
            Node current = null;
            if (size - 1 - idx > idx) {
                current = begin;
                int index = 0;
                while(index < idx) {
                    current = current.next;
                    index++;
                }
                removed_element = current.val;
            }
            else {
                current = end;
                int index = size - 1;
                while(index > idx) {
                    current = current.prev;
                    index--;
                }
                removed_element = current.val;
            }
            current.prev.next = current.next;
            current.next.prev = current.prev;
        }
        size--;
        return removed_element;
    }

    @Override
    int get(int idx) throws NoSuchElementException {
        if(idx >= size || idx < 0) {
            throw new NoSuchElementException();
        }
        Node current = null;
        int found;
        if (size - 1 - idx > idx) {
            current = begin;
            int index = 0;
            while(index < idx) {
                current = current.next;
                index++;
            }
            found = current.val;
        }
        else {
            current = end;
            int index = size - 1;
            while(index > idx) {
                current = current.prev;
                index--;
            }
            found = current.val;
        }
        return found;
    }

    @Override
    int size() {
        return size;
    }
}
