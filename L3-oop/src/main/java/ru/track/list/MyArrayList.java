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

    public MyArrayList() {
        data = new int[0];
    }

    public MyArrayList(int capacity) {
        data = new int[capacity];
    }

    @Override
    void add(int item) {
        int[] tmp = new int[data.length + 1];
        System.arraycopy(data, 0, tmp, 0, data.length);
        tmp[data.length] = item;
        data = tmp;
    }

    @Override
    int remove(int idx) throws NoSuchElementException {
        if (idx < 0 || idx > data.length)
            throw new NoSuchElementException();
        int result = data[idx];
        int[] tmp = new int[data.length - 1];
        System.arraycopy(data, 0, tmp, 0, idx);
        System.arraycopy(data, idx + 1, tmp, idx, data.length - idx - 1);
        data = tmp;
        return result;
    }

    @Override
    int get(int idx) throws NoSuchElementException {
        if (idx < 0 || idx > data.length || data.length == 0)
            throw new NoSuchElementException();
        return data[idx];
    }

    @Override
    int size() {
        return data.length;
    }
}
