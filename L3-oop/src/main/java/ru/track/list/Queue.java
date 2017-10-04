package ru.track.list;


interface Queue {
    void enqueue(int value); // поместить элемент в очередь
    int dequeu(); // вытащить первый элемент из очереди
}