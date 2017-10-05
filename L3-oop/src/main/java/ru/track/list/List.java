package ru.track.list;

import java.util.NoSuchElementException;

/**
 * Реализация List не определена до конца. Класс абстрактный - его наследники должны описать поведение
 * Однако часть методов и полей может быть описана в абстрактном классе. Нужно определить что общего у
 * ArrayList & LinkedList и вынести общую часть в класс List.
 * <p>
 * Сейчас все методы помечены как abstract - эту метку можно убирать, если вы реализовываете его в этом классе
 */
public abstract class List {

    protected int size;

    List(){
        this.size = 0;
    }

    void checkID(int idx) throws NoSuchElementException {
        if (idx >= size)
            throw new NoSuchElementException();
    }

    /**
     * Добавить элемент в конец списка
     */
    abstract void add(int item);

    /**
     * удалить элемент по индексу idx, если такого индекса нет или он невалидный,
     * то бросить ошибку, если ок - вернуть удаленный элемент
     * Чтобы бросить ошибку нужно написать throw new NoSuchElementException();
     * <p>
     * Метод, который может бросить ошибку должен быть отмечен как throws НазваниеИсключения
     * как сделано для этого метода
     */
    abstract int remove(int idx) throws NoSuchElementException;


    /**
     * Получить элемент с позиции idx, бросить исключение, если позиция невалидная
     */
    abstract int get(int idx) throws NoSuchElementException;

    /**
     * Кол-во элементов списка
     */
    int size() {
        return size;
    }
}
