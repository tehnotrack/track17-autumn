package ru.track.io;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ru.track.io.vendor.Bootstrapper;
import ru.track.io.vendor.FileEncoder;

import java.io.*;

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
        final File fin = new File(finPath);
        final File fout;

        if (foutPath != null) {
            fout = new File(foutPath);
        } else {
            fout = File.createTempFile("based_file_", ".txt");
            fout.deleteOnExit();
        }

        final int bufSize = 3000;
        final byte[] byteBuf = new byte[bufSize];
        int numOfBytes = bufSize;

        try (
                final BufferedInputStream fis = new BufferedInputStream(new FileInputStream(fin));
                final BufferedOutputStream os = new BufferedOutputStream(new FileOutputStream(fout));
        ) {
            while (fis.available() != 0) {
                numOfBytes = fis.read(byteBuf);
                base64Write(byteBuf, numOfBytes, os);
            }
        }

        return fout;
    }

    public void base64Write(byte[] b, int len, BufferedOutputStream out) throws IOException {
        int lengthOfChunk = 3;
        int lengthWithoutLastChunk = len / lengthOfChunk * lengthOfChunk;
        if (lengthWithoutLastChunk != 0) {
            for (int i = 0; i < lengthWithoutLastChunk; i += 3) {
                int part = (b[i] & 0xff) << 16 |
                        (b[i + 1] & 0xff) << 8 |
                        (b[i + 2] & 0xff);
                out.write(toBase64[part >>> 18 & 0x3f]);
                out.write(toBase64[part >>> 12 & 0x3f]);
                out.write(toBase64[part >>> 6 & 0x3f]);
                out.write(toBase64[part & 0x3f]);
            }
        }
        if ((len - lengthWithoutLastChunk) == 2) {
            int part = (b[lengthWithoutLastChunk] & 0xff) << 16 |
                    (b[lengthWithoutLastChunk + 1] & 0xff) << 8;
            out.write(toBase64[part >>> 18 & 0x3f]);
            out.write(toBase64[part >>> 12 & 0x3f]);
            out.write(toBase64[part >>> 6 & 0x3c]);
            out.write('=');
        } else if ((len - lengthWithoutLastChunk) == 1) {
            int part = (b[lengthWithoutLastChunk] & 0xff) << 16;
            out.write(toBase64[part >>> 18 & 0x3f]);
            out.write(toBase64[part >>> 12 & 0x30]);
            out.write('=');
            out.write('=');
        }
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
