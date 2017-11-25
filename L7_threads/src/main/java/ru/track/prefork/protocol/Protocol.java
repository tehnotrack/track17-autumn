package ru.track.prefork.protocol;

import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.io.Serializable;

public interface Protocol<T extends Serializable> {

    public byte[] encode(T msg) throws IOException, ProtocolException;

    @Nullable
    public T decode(byte[] bytes, Class<T> clazz) throws IOException, ProtocolException;
}
