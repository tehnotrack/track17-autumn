package ru.track.prefork.protocol;

import java.io.IOException;
import java.io.Serializable;

import org.jetbrains.annotations.Nullable;

/**
 *
 */
public interface Protocol<T extends Serializable> {

    byte[] encode(T msg) throws ProtocolException, IOException;

    @Nullable
    T decode(byte[] data) throws ProtocolException, IOException;
}
