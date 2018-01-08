package ru.track.chat.parameters;

public interface Converter<T> {
    T convert(String value, T defaultValue);
}
