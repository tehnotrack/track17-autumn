package ru.track.io;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ru.track.io.vendor.Bootstrapper;
import ru.track.io.vendor.ReferenceTaskImplementation;
import ru.track.io.vendor.FileEncoder;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;

public final class TaskImplementation implements FileEncoder {
    private static final int BASE64_ITERATION = 3;
    private static final int BYTE_COUNT = 4;
    private static final int ONES = 63;

    @NotNull
    private String fillBuffer(byte[] string, int count) {
        int buffer = 0;
        int void_symbols_count = BASE64_ITERATION - count;

        buffer |= ((string[0] & 0xFF) << 16);
        buffer |= ((string[1] & 0xFF) << 8);
        buffer |= ((string[2] & 0xFF));

        int[] bytes = new int[BYTE_COUNT];
        bytes[0] = ONES & (buffer >> 18);
        bytes[1] = ONES & (buffer >> 12);
        bytes[2] = ONES & (buffer >> 6);
        bytes[3] = (ONES & buffer);

        StringBuilder result = new StringBuilder();

        for (int i = 0; i < BYTE_COUNT - void_symbols_count; ++i) {
            result.append(toBase64[bytes[i]]);
        }

        for (int i = 0; i < void_symbols_count; ++i) {
            result.append('=');
        }

        return result.toString();
    }

    @NotNull
    private String Base64Encode(FileInputStream file) throws IOException {
        StringBuilder result = new StringBuilder();

        byte[] bytes = new byte[BASE64_ITERATION];
        int count;

        while ((count = file.read(bytes, 0, 3)) != -1) {
            result.append(fillBuffer(bytes, count));
        }

        return result.toString();
    }

    /**
     * @param finPath  where to read binary data from
     * @param foutPath where to write encoded data. if null, please create and use temporary file.
     * @return file to read encoded data from
     * @throws IOException is case of input/output errors
     */
    @NotNull
    public File encodeFile(@NotNull String finPath, @Nullable String foutPath) throws IOException {
        /* XXX: https://docs.oracle.com/javase/8/docs/api/java/io/File.html#deleteOnExit-- */

        File fout;

        if (foutPath != null) {
            fout = new File(foutPath);
        } else {
            fout = File.createTempFile("based_file_", ".txt");
            fout.deleteOnExit();
        }

        File fin = new File(finPath);

        try (
                FileInputStream finStream = new FileInputStream(fin);
                PrintWriter foutStream = new PrintWriter(fout)
        ) {
            String result = Base64Encode(finStream);

            foutStream.print(result);
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
