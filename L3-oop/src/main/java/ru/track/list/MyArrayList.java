package ru.track.list;

import java.util.NoSuchElementException;

/**
 * Должен наследовать List
 * <p>
 * Должен иметь 2 конструктора
 * - без аргументов - создает внутренний массив дефолтного размера на ваш выбор
 * - с аргументом - начальный размер массива
 */
public class MyArrayList extends List {

    private int[] values;
    private int capacity;

    public MyArrayList() {

        this.capacity = 10;
        values = new int[this.capacity];
        this.size = 0;
        
    }

    public MyArrayList(int capacity) {

        this.capacity = capacity;
        values = new int[this.capacity];
        this.size = 0;

    }

    @Override
    void add(int item) {

        if (this.size >= this.capacity) {
            if (this.capacity == 0) {
                this.capacity = 1;
            }
            this.capacity = this.capacity * 2;
            this.values = expandArray(this.values, this.size, this.capacity);
        }

        this.values[this.size++] = item;
    }

    @Override
    int remove(int idx) throws NoSuchElementException {

        int retValue;

        if (idx >= size) {
            throw new NoSuchElementException();
        }

        retValue = values[idx];

        System.arraycopy(values, idx + 1, values, idx, size - idx);

        this.size--;

        return retValue;

    }

    @Override
    int get(int idx) throws NoSuchElementException {

        if (idx >= size) {
            throw new NoSuchElementException();
        }

        return values[idx];
    }

    @Override
    int size() {
        return this.size;
    }

    static public int[] expandArray(int[] oldArray, int size, int newSize) {

        int[] newArray = new int[newSize];
        System.arraycopy(oldArray, 0, newArray, 0, size);

        return newArray;
    }
}
