package ru.track.prefork;

import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.io.OutputStream;
import java.io.Serializable;
import java.net.ProtocolException;

public interface Protocol <T extends Serializable> {
    byte[] encode (T message) throws ProtocolException, IOException;

    @Nullable
    T decode (byte[] data) throws ProtocolException, IOException;
}
