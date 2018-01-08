package ru.track.chat.parameters;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class Parameters {
    @NotNull
    public static <T> T getParameter(@Nullable String[] parameters, T defaultValue, Converter<T> converter) {
        if (parameters == null) {
            return defaultValue;
        } else {
            return converter.convert(parameters[0], defaultValue);
        }
    }
}
