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
    private static int STAND_SIZE = 10;
    private int[] arr;

    public MyArrayList() {
        arr = new int[STAND_SIZE];
    }

    public MyArrayList(int capacity) {
        arr = new int[capacity];
    }

    @Override
    void add(int item) {
        if (elem_count == arr.length) {
            int[] tempArr = arr;
            arr = new int[arr.length + STAND_SIZE];
            for (int i = 0; i < tempArr.length; i++) {
                arr[i] = tempArr[i];
            }
        }
        arr[elem_count++] = item;
    }

    @Override
    int remove(int idx) throws NoSuchElementException {
        if (elem_count <= idx) {
            throw new NoSuchElementException("Deletion from empty arraylist!");
        }
        int res = arr[idx];
        for (int i = idx; i < elem_count - 1; i++) {
            arr[i] = arr[i + 1];
        }
        elem_count--;
        return res;
    }

    @Override
    int get(int idx) throws NoSuchElementException {
        if (elem_count <= idx) {
            throw new NoSuchElementException("No such index!");
        }
        return arr[idx];
    }

    @Override
    int size() {
        return elem_count;
    }

}
