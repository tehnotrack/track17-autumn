
package ru.track.prefork.protocol;

import com.sun.istack.internal.Nullable;

import java.io.IOException;
import java.io.Serializable;


public interface Protocol<T extends Serializable> {
    byte[] encode(T msg) throws ProtocolException, IOException;

    @Nullable
    T decode(byte[] data) throws ProtocolException, IOException;


}