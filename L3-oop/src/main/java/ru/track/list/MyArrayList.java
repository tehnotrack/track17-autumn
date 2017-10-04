package ru.track.list;

import java.util.NoSuchElementException;

import static java.lang.Math.max;

/**
 * Должен наследовать List
 *
 * Должен иметь 2 конструктора
 * - без аргументов - создает внутренний массив дефолтного размера на ваш выбор
 * - с аргументом - начальный размер массива
 */
public class MyArrayList extends List {
    private final int DEFAULT_CAPACITY = 10;
    private int currentCapacity;
    private int[] array;

    public MyArrayList() {
        super();
        currentCapacity = DEFAULT_CAPACITY;
        array = new int[DEFAULT_CAPACITY];
    }

    public MyArrayList(int capacity) {
        super();
        currentCapacity = max(DEFAULT_CAPACITY, capacity);
        array = new int[currentCapacity];
    }

    @Override
    public void add(int item) {
        if (size + 1 > currentCapacity * 3 / 4) {
            currentCapacity *= 2;

            int[] destArray = new int[currentCapacity];
            System.arraycopy(array, 0, destArray, 0, size);
            array = destArray;
        }

        array[size] = item;
        size++;
    }

    @Override
    public int remove(int idx) throws NoSuchElementException {
        if (!check(idx)) {
            throw new NoSuchElementException();
        }

        int toReturn = array[idx];
        System.arraycopy(array, idx + 1, array, idx, size - idx - 1);
        size--;


        if (size < currentCapacity * 3 / 8 && currentCapacity / 2 > 10) {
            currentCapacity /= 2;

            int[] destArray = new int[currentCapacity];
            System.arraycopy(array, 0, destArray, 0, size);
            array = destArray;
        }

        return toReturn;
    }

    @Override
    public int get(int idx) throws NoSuchElementException {
        if (!check(idx)) {
            throw new NoSuchElementException();
        }

        return array[idx];
    }
}
