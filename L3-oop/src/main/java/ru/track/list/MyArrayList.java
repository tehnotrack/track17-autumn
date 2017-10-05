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
    private int Array[];
    int capacity;

    public MyArrayList() {
        this.capacity = 1;
        Array = new int [1];
    }

    public MyArrayList(int capacity) {
        this.capacity = capacity;
        Array = new int [capacity];
    }

    @Override
    void add(int item) {
        if (capacity == 0)
        {
            capacity = 2;
            Array = new int [capacity];
        }
        else if (size == capacity){
            int[] tempArray = new int [capacity * 2];

            for (int i = 0; i < capacity; i++)
                tempArray[i] = Array[i];

            capacity *= 2;
            Array = tempArray;
        }

        Array[size] = item;
        size++;
    }

    @Override
    int remove(int idx) throws NoSuchElementException {
        checkID(idx);

        int result = Array[idx];
        size--;

        for (int i = idx; i < size; i++)
            Array[i] = Array[i + 1];

        return result;
    }

    @Override
    int get(int idx) throws NoSuchElementException {
        checkID(idx);
        return Array[idx];
    }
}
