package ru.track.list;

import java.util.NoSuchElementException;

/**
 * Должен наследовать List
 * Односвязный список
 */
public class MyLinkedList extends List implements Stack, Queue
{

    /**
     * private - используется для сокрытия этого класса от других.
     * Класс доступен только изнутри того, где он объявлен
     * <p>
     * static - позволяет использовать Node без создания экземпляра внешнего класса
     */

    Node head;
    Node tail;
    int size;

    public MyLinkedList()
    {
        head=new Node();
        tail=new Node();
        head.next=tail;
        tail.prev=head;
        size=0;
    }
    private static class Node
    {
        Node prev;
        Node next;
        int val;

        Node()
        {

        }
        Node(Node prev, Node next, int val)
        {
            this.prev = prev;
            this.next = next;
            this.val = val;
        }
    }

    @Override
    void add(int item)
    {
        Node newTail=new Node();
        tail.val=item;
        tail.next=newTail;
        newTail.prev=tail;
        tail=newTail;
        ++size;
    }

    @Override
    int remove(int idx) throws NoSuchElementException
    {
        if(idx>=size || idx<0) throw new NoSuchElementException();
        Node a=head.next;
        for(int i=0;i<idx;a=a.next,++i);
        int val = a.val;
        Node prev =a.prev;
        Node next =a.next;
        prev.next=next;
        next.prev=prev;
        --size;
        return val;
    }

    @Override
    int get(int idx) throws NoSuchElementException
    {
        if(idx>=size || idx<0) throw new NoSuchElementException();
        Node a=head.next;
        for(int i=0;i<idx;a=a.next,++i);
        return a.val;
    }

    public void enqueue(int value)
    {
        add(value);
    } // поместить элемент в очередь
    public int dequeu()
    {
        return remove(0);
    }

    public void push(int value)
    {
        add(value);
    }

    public int pop()
    {
        return  remove(size-1);
    }


    @Override
    int size()
    {
        return size;
    }
}
