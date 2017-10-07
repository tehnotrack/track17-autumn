package ru.track.list;

// Стек - структура данных, удовлетворяющая правилу Last IN First OUT
interface Stack
{
    void push(int value); // положить значение наверх стека
    int pop(); // вытащить верхнее значение со стека
}