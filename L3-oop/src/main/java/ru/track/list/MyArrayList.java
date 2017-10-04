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

    private int data[];
    private int size;
    private int capacity;


    public MyArrayList() {
        this(10);
    }

    public MyArrayList(int capacity) {
        data = new int[capacity];
        this.capacity = capacity;
        size = 0;
    }

    @Override
    void add(int item) {
        if (size >= capacity) {
            int newData[] = new int[capacity * 2 + 1];
            System.arraycopy(data, 0, newData, 0, size);
            data = newData;
            capacity = 2 * capacity + 1;
        }
        data[size] = item;
        size++;
    }

    @Override
    int remove(int idx) throws NoSuchElementException {
        if (idx < 0 || idx >= size) {
            throw new NoSuchElementException();
        }
        int result = data[idx];
        for (int i = idx + 1; i < size; ++ i) {
            data[i - 1] = data[i];
        }
        size--;
        return result;
    }

    @Override
    int get(int idx) throws NoSuchElementException {
        if (idx < 0 || idx >= size) {
            throw new NoSuchElementException();
        }
        return data[idx];
    }

    @Override
    int size() {
        return size;
    }
}
