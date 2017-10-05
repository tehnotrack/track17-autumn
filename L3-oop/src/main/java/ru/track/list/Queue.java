package ru.track.list;

// Очередь - структура данных, удовлетворяющая правилу First IN First OUT
interface Queue {
    void enqueue(int value); // поместить элемент в очередь
    int dequeu(); // вытащить первый элемент из очереди
}
