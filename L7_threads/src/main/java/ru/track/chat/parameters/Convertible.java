package ru.track.chat.parameters;

public interface Convertible<T> {
    default T convert(String value, T defaultValue) {
        return (T) value;
    }
}
