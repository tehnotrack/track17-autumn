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

    int[] arr;

    public MyArrayList()
    {
        arr = new int[0];
    }

    public MyArrayList(int capacity)
    {
        arr = new int[capacity];
    }

    @Override
    void add(int item)
    {
      int tmp[] = new int[arr.length + 1];
      System.arraycopy(arr, 0, tmp, 0, arr.length);
      tmp[arr.length] = item;
      arr = tmp;
    }

    @Override
    int remove(int idx) throws NoSuchElementException {
        if (idx > arr.length || idx < 0) {
            throw new NoSuchElementException();
        }
        int value = arr[idx];
        int tmp[] = new int[arr.length - 1];
        System.arraycopy(arr, 0, tmp, 0, idx);
        System.arraycopy(arr, idx+1, tmp, idx, arr.length - idx -1);
        arr=tmp;
        return value;
    }

    @Override
    int get(int idx) throws NoSuchElementException
    {
        if (idx > arr.length || idx < 0 || arr.length == 0){
            throw new NoSuchElementException();
        }
        return arr[idx];
    }

    @Override
    int size()
    {
        return arr.length;
    }
}
