package ru.track.list;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.NoSuchElementException;

/**
 * Должен наследовать List
 *
 * Должен иметь 2 конструктора
 * - без аргументов - создает внутренний массив дефолтного размера на ваш выбор
 * - с аргументом - начальный размер массива
 */
public class MyArrayList extends List {
    LinkedList<Integer> list = new LinkedList<>();
    public MyArrayList() {
        LinkedList<Integer> list = new LinkedList<>();
    }

    public MyArrayList(int capacity) {
        LinkedList<Integer> list = new LinkedList<>();
    }

    @Override
    void add(int item) {
        list.add(item);
    }

    @Override
    int remove(int idx) throws NoSuchElementException {
        int tmp = list.get(idx);
        list.remove(idx);
        int []arr = new int[list.size()];
        for (int i =0; i< arr.length;i++) {
            arr[i] = list.get(i);
        }
        return tmp;
    }

    @Override
    int get(int idx) throws NoSuchElementException {
          if (idx < 0 || idx >= list.size()) {
            throw new NoSuchElementException();
          }
          return list.get(idx);
    }

    @Override
    int size() {
        return list.size();
    }
}
