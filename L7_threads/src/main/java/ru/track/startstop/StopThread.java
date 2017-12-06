package ru.track.startstop;

import java.util.Random;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

/**
 *  Несколько способоа остановить поток
 */
public class StopThread {

    public static void randomSleep() {
        // В любом месте можно узнать, в каком потоке исполняется код
        String threadName = Thread.currentThread().getName();
        try {
            Random r = new Random();
            int delay = r.nextInt(2000);
            System.out.println(String.format("Thread [%s] doWork: %d", threadName, delay));
            TimeUnit.MILLISECONDS.sleep(r.nextInt(delay));
        } catch (InterruptedException e) {
            System.out.println(String.format("Thread [%s] doWork: INTERRUPTED", threadName));
        }
    }

    // Остановка с выставлением флага
    static class FlagThread extends Thread {
        private volatile boolean pleaseStop;

        public FlagThread() {
            setName("FLAG_THREAD");
        }

        @Override
        public void run() {
            while (!pleaseStop) {
                randomSleep();
            }
            // освобождение ресурсов
        }

        public void stopThread() {
            pleaseStop = true;
        }
    }

    // С помощью стандартного механизма прерывания
    static class InterThread extends Thread {

        public InterThread() {
            setName("INTERRUPT_THREAD");
        }
        @Override
        public void run() {
            while (!isInterrupted()) {
                // В любом месте можно узнать, в каком потоке исполняется код
                String threadName = Thread.currentThread().getName();
                try {
                    Random r = new Random();
                    int delay = r.nextInt(2000);
                    System.out.println(String.format("Thread [%s] doWork: %d", threadName, delay));
                    TimeUnit.MILLISECONDS.sleep(r.nextInt(delay));
                } catch (InterruptedException e) {
                    System.out.println(String.format("Thread [%s] doWork: INTERRUPTED", threadName));
                    e.printStackTrace();
                    return;
                }
            }
        }
    }

    // Без какой-либо обработки прерывания
    static class DummyThread extends Thread {
        public DummyThread() {
            setName("DUMMY_THREAD");
        }
        @Override
        public void run() {
            while (true) {
                randomSleep();
            }
        }
    }

    public static void flagThread() {
        FlagThread flagThread = new FlagThread();
        flagThread.start();

        Scanner scanner = new Scanner(System.in);
        scanner.next();
        System.out.println("Stopping>>>");
        flagThread.stopThread();
    }

    public static void interruptThread() {
        Thread thread = new InterThread();
        thread.start();

        Scanner scanner = new Scanner(System.in);
        scanner.next();
        System.out.println("Stopping>>>");
        thread.interrupt();
    }

    public static void dummyThread() {
        Thread thread = new DummyThread();
        thread.start();

        Scanner scanner = new Scanner(System.in);
        scanner.next();
        System.out.println("Stopping>>>");
        thread.interrupt();
    }

    public static void main(String[] args) throws Exception {
//        flagThread();
//        interruptThread();
        dummyThread();
    }

}
