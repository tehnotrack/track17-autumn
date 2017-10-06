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

    private static final int DEFAULT_CAPACITY = 100;

    private int[] buffer;
    private int pointer = 0;
    private int capacity;

    public MyArrayList() {
        this.buffer   = new int[DEFAULT_CAPACITY];
        this.capacity = DEFAULT_CAPACITY;
    }

    public MyArrayList(int capacity) {
        this.buffer   = new int[capacity];
        this.capacity = capacity;
    }

    @Override
    void add(int item) {
        try {
            this.buffer[this.pointer] = item;
        }
        catch (ArrayIndexOutOfBoundsException e) {
            ++this.capacity;
            this.buffer = resize(this.buffer, this.capacity);

            this.add(item);

            return;
        }

        ++this.pointer;
    }

    @Override
    int remove(int idx) throws NoSuchElementException {
        if (this.checkIndex(idx)) {
            throw new NoSuchElementException();
        }

        int removedElement;

        try {
            removedElement = this.buffer[idx];
        }
        catch (ArrayIndexOutOfBoundsException e) {
            throw new NoSuchElementException();
        }

        for (int i = idx; i < this.pointer - 1; ++i) {
            this.buffer[i] = this.buffer[i + 1];
        }

        this.buffer[--this.pointer] = 0;

        --this.capacity;

        return removedElement;
    }

    @Override
    int get(int idx) throws NoSuchElementException {
        int result;

        if (this.checkIndex(idx)) {
            throw new NoSuchElementException();
        }

        try {
            result = this.buffer[idx];
        }
        catch (ArrayIndexOutOfBoundsException e) {
            throw new NoSuchElementException();
        }

        return result;
    }

    @Override
    int size() {
        return this.pointer;
    }

    private boolean checkIndex(int idx) {
        return idx < 0 || idx > pointer - 1;
    }

    static private int[] resize(int[] source, int size) {
        int sourceLength = source.length;
        if (size < sourceLength) {
            return source;
        }

        int[] result = new int[size];

        for (int i = 0; i < sourceLength; ++i) {
            result[i] = source[i];
        }

        return result;
    }
}
