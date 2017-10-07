package ru.track.io;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ru.track.io.vendor.Bootstrapper;
import ru.track.io.vendor.FileEncoder;

import java.io.*;
import java.util.Objects;

public final class TaskImplementation implements FileEncoder {

    /**
     * @param finPath  where to read binary data from
     * @param foutPath where to write encoded data. if null, please create and use temporary file.
     * @return file to read encoded data from
     * @throws IOException is case of input/output errors
     */
    @NotNull
    public File encodeFile(@NotNull String finPath, @Nullable String foutPath) throws IOException {
        final File fin = new File(finPath);

        final File fout;

        /* XXX: https://docs.oracle.com/javase/8/docs/api/java/io/File.html#deleteOnExit-- */
        if (Objects.isNull(foutPath)) {
            fout = File.createTempFile("default_picture", ".txt");
            fout.deleteOnExit();
        } else {
            fout = new File(foutPath);
        }

        try (BufferedInputStream input = new BufferedInputStream(new FileInputStream(fin));
             final FileWriter fw = new FileWriter(fout)) {

            byte[] bytes = new byte[3];
            int readingBytes;

            //читаем 3 байта из входного потока
            while ((readingBytes = input.read(bytes, 0, 3)) > 0) {
                int threeBytes = 0;
                for (int i = 0; i < readingBytes; i++) {
                    threeBytes = threeBytes | (bytes[i] & 0xFF) << (16 - 8 * i);
                }

                //4 байта, каждый символ равен '=', пишем 4 байта в выходной поток
                char[] encodedData = new char[]{'=', '=', '=', '='};
                for (int i = 0; i <= readingBytes; i++) {
                    encodedData[i] = toBase64[threeBytes >> (18 - 6 * i) & 0x3F];
                }
                fw.write(encodedData);
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