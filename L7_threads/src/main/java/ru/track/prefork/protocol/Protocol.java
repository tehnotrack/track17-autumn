package ru.track.prefork.protocol;

import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;

public interface Protocol<T extends Serializable> {

    public byte[] encode(T msg) throws IOException, ProtocolException;

    @Nullable
    public T decode(InputStream is, Class<T> clazz) throws IOException, ProtocolException;
}
