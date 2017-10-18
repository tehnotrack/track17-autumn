package ru.track.ex;

import java.util.concurrent.atomic.AtomicInteger;

import org.junit.Assert;
import org.junit.Test;

/**
 *
 */
public class TaskTest {

    @Test(expected = NumberFormatException.class)
    public void name() {
        Integer.parseInt("a4");
    }

    @Test
    public void t1() {

        String str = "before";
        try {
            str = "inTry";
            throw new Exception();
        } catch (Exception e) {
            str = "inCatch";
        } finally {
            str = "finally";
        }
        Assert.assertEquals("finally", str);
    }

    @Test
    public void t2() {
        Assert.assertEquals("inCatch", t2_inner0());

        Assert.assertEquals(-1, t2_inner1().get());
    }

    public String t2_inner0() {
        String str = "before";
        try {
            throw new Exception();
        } catch (Exception e) {
            str = "inCatch";
            return str;
        } finally {
            str = "finally";
        }
    }


    public AtomicInteger t2_inner1() {
        AtomicInteger val = new AtomicInteger(0);
        try {
            throw new Exception();
        } catch (Exception e) {
            val.set(1);
            return val;
        } finally {
            val.set(-1);
        }
    }
}