package ru.track.list;

import java.util.NoSuchElementException;

/**
 * Должен наследовать List
 * Односвязный список
 */
public class MyLinkedList extends List implements Stack, Queue{

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

    private Node first = null;
    private Node last = null;

    @Override
    void add(int item) {
        if(this.first == null && this.last == null){
            this.last = this.first = new Node(null, null, item);
        }
        else {
            Node temp = new Node(this.last, null, item);
            (this.last).next = temp;
            this.last = temp;
        }
        this.size++;
    }

    @Override
    int remove(int idx) throws NoSuchElementException {
        int i = 0;
        Node temp = this.first;
        if(temp == null)
            throw new NoSuchElementException();
        while(i < idx){
            if(temp.next == null)
                throw new NoSuchElementException();
            temp = temp.next;
            i++;
        }
        if(temp.prev != null)
            (temp.prev).next = temp.next;
        if(temp.next != null)
            (temp.next).prev = temp.prev;
        this.size--;
        return temp.val;
    }

    @Override
    int get(int idx) throws NoSuchElementException {
        int i = 0;
        Node temp = this.first;
        if(temp == null)
            throw new NoSuchElementException();
        while(i < idx){
            if(temp.next == null)
                throw new NoSuchElementException();
            temp = temp.next;
            i++;
        }
        return temp.val;
    }

    @Override
    public void push(int value){
        this.add(value);
    }

    @Override
    public int pop(){
        return remove(this.size);
    }

    @Override
    public void enqueue(int value){
        this.add(value);
    }

    @Override
    public int dequeu(){
        return remove(0);
    }
}
