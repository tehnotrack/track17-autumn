package ru.track.io.vendor;

import org.apache.commons.io.IOUtils;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.util.Base64;

public class ReferenceTaskImplementation implements FileEncoder {

    @NotNull
    public File encodeFile(@NotNull String finPath, @Nullable String foutPath) throws IOException {
        final File fin = new File(finPath);
        final File fout;

        if (foutPath != null) {
            fout = new File(foutPath);
        } else {
            fout = File.createTempFile("based_file_", ".txt");
            fout.deleteOnExit();
        }

        final Base64.Encoder encoder = Base64.getEncoder();
        try (
                final FileInputStream fis = new FileInputStream(fin);
                final OutputStream os = encoder.wrap(new FileOutputStream(fout));
        ) {
            int bytesCopied = IOUtils.copy(fis, os); // result unused
        }

        return fout;
    }

}
