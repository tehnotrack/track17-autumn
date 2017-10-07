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

    public Node head = null;
    public Node tail = null;
    int size = 0;

    @Override
    void add(int item) {
        if (size == 0){
            head = new Node(null,null,item);
            tail = head;
            size++;
            return;
        } else if (size ==1){
            tail = new Node(tail,null, item);
            head.next = tail;
            size++;
            return;
        } else {
            tail = new Node(tail,null, item);
            tail.prev.next = tail;
            size++;
            return;
        }
    }

    @Override
    int remove(int idx) throws NoSuchElementException {
        if (head == null){
            throw new NoSuchElementException();
        }else{
            Node r = head;
            for (int i = 0; i < idx; i++){
                r = r.next;
            }
            if(r.prev!=null){
                if (r.next!=null) {
                    r.prev.next = r.next;
                    r.next.prev = r.prev;
                    size--;
                    return r.val;
                } else {
                    r.prev.next = null;
                    tail = r.prev;
                    size--;
                    return r.val;
                }
            }else{
                if(r.next != null){
                    r.next.prev = null;
                    head = r.next;
                    size--;
                    return r.val;
                }
            }
            size--;
            return r.val;
        }
    }

    @Override
    int get(int idx) throws NoSuchElementException {
        if (idx < 0 || idx >= size()) {
            throw new NoSuchElementException();
        }else if (idx == 0){
            Node r = head;
            return r.val;
        }else{
            Node r = head;
            for (int i = 0; i < idx; i++) {
                r = r.next;
            }
            return r.val;
        }
    }

    @Override
    int size() {return size;}
}
