package ru.track.list;

import java.util.NoSuchElementException;

/**
 * Должен наследовать List
 *
 * Должен иметь 2 конструктора
 * - без аргументов - создает внутренний массив дефолтного размера на ваш выбор
 * - с аргументом - начальный размер массива
 */
public class MyArrayList extends List {
    int size;
    int capacity;
    int [] my_List;

    public MyArrayList() {
        size = 0;
        capacity = 10;
        my_List = new int [capacity];
        for (int i = 0; i < size; i++) my_List[i] = 0;
    }

    public MyArrayList(int capacity) {
        size = capacity;
        this.capacity = capacity;
        my_List = new int [capacity];
        for (int i = 0; i < size; i++) my_List[i] = 0;
    }

    @Override
    void add(int item) {
        if (size >= capacity) {
            capacity += 10;
            my_List = new int [capacity];
            my_List[size] = item;
            size++;
        }
        else if (capacity == 0) {
            capacity+=10;
            my_List = new int [capacity];
            my_List[size] = item;
            size++;
        }
        else {
            my_List[size] = item;
            size++;
        }
    }

    @Override
    int remove(int idx) throws NoSuchElementException {
        if (idx <= size) {
            int ret = my_List[idx];
            for (int i = size-1; i > idx; i--) {
                my_List[i-1] = my_List[i];
            }
            size--;
            return ret;
        }
        else throw new NoSuchElementException();
    }

    @Override
    int get(int idx) throws NoSuchElementException {
        if (idx < size) {
            return my_List[idx];
        }
        else throw new NoSuchElementException();
    }

    @Override
    int size() {
        return size;
    }
}
