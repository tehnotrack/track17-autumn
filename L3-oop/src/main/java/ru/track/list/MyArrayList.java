package ru.track.list;

import java.util.ArrayList;
import java.util.NoSuchElementException;

/**
 * Должен наследовать List
 *
 * Должен иметь 2 конструктора
 * - без аргументов - создает внутренний массив дефолтного размера на ваш выбор
 * - с аргументом - начальный размер массива
 */
public class MyArrayList extends List {

    public int[] list;
    public int size;
    public int cap;

    public MyArrayList() {
        int [] list = new int[10];
        cap = 10;
        this.size = 0;
    }

    public MyArrayList(int capacity) {
        list = new int[capacity];
        this.size = capacity;
        this.cap = capacity;
    }

    @Override
    void add(int item) {
        int[] list2 = new int[this.size + 1];
        if (size != 0)
            System.arraycopy(list, 0, list2, 0, size);
        list2[size] = item;
        list = list2;
        size += 1;
    }

    @Override
    int remove(int idx) throws NoSuchElementException {
        if ((idx <  size) & (idx >= 0)){
            if (size > 1){
                int[] list2 = new int[size - 1];
                int a = list[idx];
                System.arraycopy(list, 0, list2, 0 , idx);
                System.arraycopy(list, idx + 1, list2, idx  , size - idx - 1);
                list = list2;
                size--;
                return a;
            }
            else {
                size--;
                return list[idx];
            }
        }
        else throw new NoSuchElementException();
    }

    @Override
    int get(int idx) throws NoSuchElementException {
        if ((idx <  size) & (idx >= 0)){
            return list[idx];
        }
        else throw new NoSuchElementException();

    }

    @Override
    int size() {
        return size;
    }
    public static void main(String args[])
    {
        MyArrayList list = new MyArrayList();
        list.add(5);
        list.add(8);
        list.add(99);
        list.remove(1);
        System.out.println(list.get(0));
        System.out.println(list.get(1));
    }
}
