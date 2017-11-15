package ru.track.prefork.Protocol;

import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.io.Serializable;
import java.net.ProtocolException;

public interface Protocol<T extends Serializable> {
    byte[] encode(T msg) throws ProtocolException, IOException;

    @Nullable
    T decode(byte[] data) throws ProtocolException, IOException;
}