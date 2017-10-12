package ru.track.io;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ru.track.io.vendor.Bootstrapper;
import ru.track.io.vendor.FileEncoder;
import ru.track.io.vendor.ReferenceTaskImplementation;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

public final class TaskImplementation implements FileEncoder {

    /**
     * @param finPath  where to read binary data from
     * @param foutPath where to write encoded data. if null, please create and use temporary file.
     * @return file to read encoded data from
     * @throws IOException is case of input/output errors
     */

    @NotNull
    public File encodeFile(@NotNull String finPath, @Nullable String foutPath) throws IOException {
        File fin = new File(finPath);
        File fout;
        if (foutPath == null) {
            fout = File.createTempFile("Output", ".txt");
            fout.deleteOnExit();
        } else {
            fout = new File(foutPath);
        }
        FileInputStream input = new FileInputStream(fin);
        try (BufferedInputStream buff = new BufferedInputStream(input);
             FileWriter writer = new FileWriter(fout)) {
            while (true) {
                int byte1 = buff.read();
                if (byte1 == -1) {
                    break;
                }
                int byte2 = buff.read();
                int byte3 = buff.read();
                int unit = ((byte1 & 0xff) << 16) | ((Math.max(byte2, 0) & 0xff) << 8) | (Math.max(byte3, 0) & 0xff);
                writer.write(toBase64[(unit >> 18) & 63]);
                writer.write(toBase64[(unit >> 12) & 63]);
                if (byte2 == -1) {
                    writer.write('=');
                } else {
                    writer.write(toBase64[(unit >> 6) & 63]);
                }
                if (byte3 == -1) {
                    writer.write('=');
                } else {
                    writer.write(toBase64[unit & 63]);
                }
            }
        }
        return fout;
    }

    private static final char[] toBase64 = {
            'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M',
            'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z',
            'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm',
            'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z',
            '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '+', '/'
    };

    public static void main(String[] args) throws IOException {
        final FileEncoder encoder = new TaskImplementation();
        // NOTE: open http://localhost:9000/ in your web browser
        new Bootstrapper(args, encoder).bootstrap(9000);
    }

}
