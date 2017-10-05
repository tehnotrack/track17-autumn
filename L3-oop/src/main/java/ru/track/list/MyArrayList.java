package ru.track.list;

import java.util.Arrays;
import java.util.NoSuchElementException;

/**
 * Должен наследовать List
 * <p>
 * Должен иметь 2 конструктора
 * - без аргументов - создает внутренний массив дефолтного размера на ваш выбор
 * - с аргументом - начальный размер массива
 */
public class MyArrayList extends List {
    private static final int[] EMPTY_LIST = {}; // пустой массив, который будем возвращать при создании пустого листа
    private int size;
    private int[] ListData; // массив, в котором будем хранить элементы

    public MyArrayList() {
        this.ListData = EMPTY_LIST;
    }

    public MyArrayList(int capacity) {
        if (capacity < 0) throw new IllegalArgumentException("Cannot create ArrayList");
        else this.ListData = new int[capacity];
    }

    @Override
    public void add(int item) {
        if (size == ListData.length) increaseCapacity();
        ListData[size++] = item;
    }

    @Override
    public int remove(int idx) throws NoSuchElementException {
        int removedElement = ListData[idx];
        if ((size - idx - 1) > 0) System.arraycopy(ListData, idx + 1, ListData, idx, size - idx - 1);
        ListData[--size] = 0;
        return removedElement;
    }

    @Override
    public int get(int idx) throws NoSuchElementException {
        if (idx < 0 || idx >= size) {
            throw new NoSuchElementException("invalid index");
        }
        return ListData[idx];
    }

    @Override
    public int size() {
        return size;
    }

    /*Вспомогательный метод, увеличивает размер ListData*/
    private void increaseCapacity() {
        int newCapacity = ListData.length * 2 + 1;
        ListData = Arrays.copyOf(ListData, newCapacity);
    }
}
