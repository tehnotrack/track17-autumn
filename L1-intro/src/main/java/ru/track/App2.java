package ru.track;

import org.apache.commons.lang3.StringUtils;

public class App2 {

    public static void main(String[] args) {
        String str = StringUtils.capitalize(args[0]);
        System.out.println(str);
    }
}
