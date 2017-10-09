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
    int sz = 0;



    @Override
    void add(int item) {
        if (head == null) {
            head = new Node(null, null, item);
            sz++;
            }
        else {
            Node tmp = head;
            while (tmp.next != null) {
                tmp = tmp.next;
            }
            tmp.next = new Node(tmp, null, item);
            sz++;
        }
    }

    @Override
    int remove(int idx) throws NoSuchElementException {
        if (idx < 0 || sz== 0 || idx > sz)
            throw new NoSuchElementException();
        Node tmp = head;
        while (idx != 0 && tmp.next != null) {
            idx--;
            tmp = tmp.next;
            }
        if (idx == 0) {
            int result = tmp.val;
            if (tmp.prev == null) {
                head=null;
                sz--;
                return result;
                }
            if (tmp.next == null) {
                tmp.prev.next = null;
                sz--;
                return result;
                }
            tmp.prev.next = tmp.next;
            sz--;
            return result;
            }
        throw new NoSuchElementException();
    }

    @Override
    int get(int idx) throws NoSuchElementException {

        if (idx < 0 || sz == 0 || idx > sz)
            throw new NoSuchElementException();
        Node tmp = head;
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

        return sz;
    }
}


