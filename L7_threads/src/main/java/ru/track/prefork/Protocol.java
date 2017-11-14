package ru.track.prefork;

import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.io.Serializable;

public interface Protocol<T extends Serializable> {

    byte[] encode(T msg) throws IOException;

    @Nullable
    T decode(byte[] data) throws IOException;
}
