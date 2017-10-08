package ru.track.io.vendor;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;

@FunctionalInterface
public interface FileEncoder {

    @NotNull
    File encodeFile(@NotNull String finPath, @Nullable String foutPath) throws IOException;

}
