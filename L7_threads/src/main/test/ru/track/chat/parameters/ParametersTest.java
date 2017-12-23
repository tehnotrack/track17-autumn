package ru.track.chat.parameters;

import org.junit.Assert;
import org.junit.Test;

public class ParametersTest {
    @Test
    public void getParametersLongTest() {
        Long rightString = Parameters.getParameter(new String[]{"1971"}, 19L, new LongConverter());
        Long wrongString = Parameters.getParameter(new String[]{"12.4"}, 20L, new LongConverter());
        Long wrongString2 = Parameters.getParameter(new String[]{"124j"}, 21L, new LongConverter());
        
        Assert.assertEquals(rightString.longValue(), 1971);
        Assert.assertEquals(wrongString.longValue(), 20);
        Assert.assertEquals(wrongString2.longValue(), 21);
    }
    
    @Test
    public void getParametersStringTest() {
        String str1 = Parameters.getParameter(new String[]{"197198739 '';';';;'1"}, "def", new StringConverter());
        String str2 = Parameters.getParameter(new String[]{"12.4", "asdkjbnfkjdn"}, "def", new StringConverter());
        String str3 = Parameters.getParameter(
                new String[]{"124j", " akdjkj h kjhou 8989q", "lsidh uy87y 8237y87 y'ei'pa'd"}, "def",
                new StringConverter()
        );
        
        Assert.assertEquals(str1, "197198739 '';';';;'1");
        Assert.assertEquals(str2, "12.4");
        Assert.assertEquals(str3, "124j");
    }
    
    static class LongConverter implements Convertible<Long> {
        @Override
        public Long convert(String value, Long defaultValue) {
            try {
                return Long.parseLong(value);
            } catch (NumberFormatException e) {
                return defaultValue;
            }
        }
    }
    
    static class StringConverter implements Convertible<String> {
    }
}
