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

    private int[] arr;

    public MyArrayList() {
        this.arr = new int[50];
    }

    public MyArrayList(int capacity) {
        this.arr = new int[capacity];
    }

    @Override
    void add(int item) {
        if(this.size < this.arr.length) {
            this.arr[this.size] = item;
            this.size++;
        }
        else {
            int[] temp = new int[size + 1];
            System.arraycopy(this.arr, 0, temp, 0, this.arr.length);
            temp[this.size] = item;
            this.size++;
            this.arr = temp;
        }
    }

    @Override
    int remove(int idx) throws NoSuchElementException {
        int temp;
        if(idx < this.size) {
            temp = this.arr[idx];
            System.arraycopy(this.arr, idx + 1, this.arr, idx, this.size - 1 - idx);
            this.size--;
            return temp;
        }
        else throw new NoSuchElementException();
    }

    @Override
    int get(int idx) throws NoSuchElementException {
        if(idx < this.size)
            return this.arr[idx];
        else throw new NoSuchElementException();
    }
}
