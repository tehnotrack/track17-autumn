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

    public MyArrayList() {

    }

    public MyArrayList(int capacity) {

    }

    @Override
    void add(int item) {

    }

    @Override
    int remove(int idx) throws NoSuchElementException {
        return 0;
    }

    @Override
    int get(int idx) throws NoSuchElementException {
        return 0;
    }

    @Override
    int size() {
        return 0;
    }
}
