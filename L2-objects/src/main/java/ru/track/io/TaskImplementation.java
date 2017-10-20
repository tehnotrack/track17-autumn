package ru.track.io;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ru.track.io.vendor.Bootstrapper;
import ru.track.io.vendor.FileEncoder;
import ru.track.io.vendor.ReferenceTaskImplementation;

import java.io.*;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

public final class TaskImplementation implements FileEncoder {

    /**
     * @param finPath  where to read binary data from
     * @param foutPath where to write encoded data. if null, please create and use temporary file.
     * @return file to read encoded data from
     * @throws IOException is case of input/output errors
     */
    @NotNull
    public File encodeFile(@NotNull String finPath, @Nullable String foutPath) throws IOException {
        /* XXX: https://docs.oracle.com/javase/8/docs/api/java/io/File.html#deleteOnExit-- */
        File fin = new File(finPath);

        File fout;
        if (foutPath == null) {
            fout = File.createTempFile("TempFile", ".txt");
            fout.deleteOnExit();
        } else {
            fout = new File(foutPath);
        }

        try(BufferedInputStream  bis = new BufferedInputStream  (new FileInputStream(fin));
            BufferedOutputStream bos = new BufferedOutputStream (new FileOutputStream(fout)))
        {
            byte[] inputBytes  = new byte[3];
            byte[] outputBytes = new byte[4];
            int n, m;

            while (true) {
                java.util.Arrays.fill(inputBytes, (byte)0);

                if ((m = bis.read(inputBytes, 0, 3)) <= 0)
                    break;

                n = ((inputBytes[0] & 0xFF) << 16) |
                        ((inputBytes[1] & 0xFF) << 8) |
                        ((inputBytes[2] & 0xFF));

                outputBytes[0] = (byte) toBase64[n >> 18 & 0x3F];
                outputBytes[1] = (byte) toBase64[n >> 12 & 0x3F];
                outputBytes[2] = (byte) (m < 2 ? '=' : toBase64[n >> 6 & 0x3F]);
                outputBytes[3] = (byte) (m < 3 ? '=' : toBase64[n & 0x3F]);

                bos.write(outputBytes, 0, outputBytes.length);
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
