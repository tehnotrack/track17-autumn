package ru.track.startstop;

import java.util.Random;
import java.util.concurrent.TimeUnit;

/**
 * Операции start/sleep/join
 */
public class StartThread {

    public static void doWork() {
        try {
            // В любом месте можно узнать, в каком потоке исполняется код
            String threadName = Thread.currentThread().getName();
            Random r = new Random();
            for (int i = 0; i < 7; i++) {
                System.out.println(String.format("Thread [%s] doWork: %d", threadName, i));
                TimeUnit.MILLISECONDS.sleep(r.nextInt(2000));
            }
            System.out.println(String.format("Thread [%s] doWork: FINISHED", threadName));
        } catch (Exception e) {/*ignored*/}
    }

    public static void main(String[] args) throws Exception {
//        inParallel();
        join();

    }

    /*
    Код исполнется в потоке main и в кастомном потоке в параллель
     */
    private static void inParallel() {
        Thread t1 = new Thread(() -> doWork());
        t1.setName("T1");
        t1.start();

        doWork();
        System.out.println("main exit");
    }

    /*
    Вызывающий поток ждет окончания потока, на котором он позвал join()
     */
    static void join() throws Exception {
        Thread t1 = new Thread() {
            @Override
            public void run() {
                doWork();
            }
        };
        t1.setName("T1");
        System.out.println("Starting thread...");
        t1.start();
        System.out.println("Joining...");
        //t1.join(); //
        System.out.println("Joined");
    }

}
