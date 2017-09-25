package ru.track.demos;

import org.openjdk.jol.info.ClassLayout;

public class ObjectLayoutDemo {

    private final Object dummy = new Object();

    // -XX:+UseCompressedOops
    public static void main(String[] args) throws ClassNotFoundException {
        final ObjectLayoutDemo demo = new ObjectLayoutDemo();
        System.out.println(demo.sample().toPrintable());
    }

    /**
     * @return a sample to play with
     */
    private ClassLayout sample() throws ClassNotFoundException {
//        return layout(42);
//        return layout(42L);
//        return layout(0.123);
//        return layout(0.123f);
//        return layout((byte) 0xFF);
//        return layout('x');
//        return layout("helloworld");
//        return layout(new char[]{'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l'}); // 12 chars! try less?
//        return layout(this.dummy);
//        return layout(this);
        return layout(Class.forName("java.lang.String"));
//        return layout(Integer.parseInt("255"));
//        return layout(Long.parseLong("DEADBEEF", 0x10));
//        return layout(Class.class);
    }

    private static ClassLayout layout(Class klass) {
        return ClassLayout.parseClass(klass);
    }

    private static ClassLayout layout(Object instance) {
        return ClassLayout.parseInstance(instance);
    }

}
