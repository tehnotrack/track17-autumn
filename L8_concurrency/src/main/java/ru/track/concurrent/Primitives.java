package ru.track.concurrent;

import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

/**
 *
 */
public class Primitives {

    static class Worker extends Thread {
        private Semaphore semaphore;

        public Worker(Semaphore semaphore) {
            this.semaphore = semaphore;
        }

        @Override
        public void run() {
            try {
                String name = Thread.currentThread().getName();
                System.out.println(String.format("%s: waiting acquire...", name));
                semaphore.acquire();
                System.out.println(String.format("%s: acquired", name));
                TimeUnit.SECONDS.sleep(2);
                semaphore.release();
                System.out.println(String.format("%s: released", name));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    static void semaphore() {
        int permit = 2;
        final Semaphore semaphore = new Semaphore(permit);
        for (int i = 0; i < permit + 1; i++) {
            new Worker(semaphore).start();
        }
    }

    public static void main(String[] args) {
        semaphore();
    }
}
