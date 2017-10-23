package ru.track.ex;

/**
 *
 */
public class Task {

    static class MyException extends Exception {
        public MyException(String message) {
            super(message);
        }

        public MyException(Throwable cause) {
            super(cause);
        }

        public MyException(String message, Throwable cause) {
            super(message, cause);
        }
    }

    public static void main(String[] args) {
        try {
            m0();
        } catch (MyException e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
    }

    static void m0() throws MyException {
        m1();
    }

    static void m1() throws MyException {
        try {
            m2();
        } catch (MyException e) {
            throw new MyException("m2()", e);
        }
    }

    static void m2() throws MyException {
        m3();
    }

    static void m3() throws MyException {
        throw new MyException("m3()");
    }
}
