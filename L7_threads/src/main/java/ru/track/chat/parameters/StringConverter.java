package ru.track.chat.parameters;

public class StringConverter implements Converter<String> {
    @Override
    public String convert(String value, String defaultValue) {
        return value;
    }
}
