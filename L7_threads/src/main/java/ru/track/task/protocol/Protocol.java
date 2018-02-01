package ru.track.task.protocol;

import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.io.Serializable;

/**
 *
 */
public interface Protocol {

    byte[] encode(Message msg) throws ProtocolException, IOException;

    @Nullable
    Message decode(byte[] data) throws ProtocolException, IOException;
}
