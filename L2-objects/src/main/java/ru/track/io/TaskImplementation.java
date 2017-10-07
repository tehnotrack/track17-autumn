package ru.track.io;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ru.track.io.vendor.Bootstrapper;
import ru.track.io.vendor.FileEncoder;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
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
        final File fout;

        /* XXX: https://docs.oracle.com/javase/8/docs/api/java/io/File.html#deleteOnExit-- */
        if (Objects.isNull(foutPath)) {
            fout = File.createTempFile("default_picture", ".txt");
            fout.deleteOnExit();
        }
        else {
            fout = new File(foutPath);
        }

        try (final FileOutputStream os = new FileOutputStream(fout)) {
            os.write(encode(Files.readAllBytes(Paths.get(finPath))).getBytes());
        } catch (IOException e) {
            throw new RuntimeException("Cannot write to this file");
        }

        return fout;
    }

    private static String encode(byte[] buf) {
        char[] bytes = new char[((buf.length + 2) / 3) * 4];
        int a = 0;
        int i = 0;
        while (i < buf.length) {
            byte b0 = buf[i++];
            byte b1 = (i < buf.length) ? buf[i++] : 0;
            byte b2 = (i < buf.length) ? buf[i++] : 0;

            bytes[a++] = toBase64[(b0 >> 2) & 0x3F];
            bytes[a++] = toBase64[((b0 << 4) | ((b1 & 0xFF) >> 4)) & 0x3F];
            bytes[a++] = toBase64[((b1 << 2) | ((b2 & 0xFF) >> 6)) & 0x3F];
            bytes[a++] = toBase64[b2 & 0x3F];
        }

        switch (buf.length % 3) {
            case 1:
                bytes[--a] = '=';
            case 2:
                /* fallthrough */
                bytes[--a] = '=';
                break;
        }
        return new String(bytes);
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