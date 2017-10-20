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

    int[] data;
    int size;
    int capacity;

    public MyArrayList() {
        this(8);
    }

    public MyArrayList(int capacity) {
        if(capacity == 0)
        {
            data = new int[8];
            this.capacity = 8;
        }
        else
        {
            data = new int[capacity];
            this.capacity = capacity;
        }

    }

    @Override
    void add(int item) {
        if (size >= capacity) {
            int[] temp = new int[capacity * 2];
            //System.arraycopy(data, 0, temp, 0, size);
            for(int i = 0; i < size; i++)
            {
                temp[i] = data[i];
            }

            data = temp;
            capacity *= 2;
        }

        data[size] = item;
        size++;
    }

    @Override
    int remove(int idx) throws NoSuchElementException {
        if (idx < 0 || idx >= size) {
            throw new NoSuchElementException();
        }


        for (int i = idx; i < size - 1; i++) {
            int a = data[i];
            data[i] = data[i + 1];
            data[i + 1] = a;
        }

        size--;
        return data[size];
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
