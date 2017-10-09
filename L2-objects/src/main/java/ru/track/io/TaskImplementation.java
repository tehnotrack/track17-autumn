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
        try (
                final BufferedInputStream fis = new BufferedInputStream(new FileInputStream(fin));
                final BufferedOutputStream os = new BufferedOutputStream(new FileOutputStream(fout));
        ) {
            byte a, b, c;
            while (fis.available() >= 3) {
                a = (byte) fis.read();
                b = (byte) fis.read();
                c = (byte) fis.read();
                int part = (a & 0xff) << 16
                        | (b & 0xff) << 8
                        | (c & 0xff);
                os.write(toBase64[part >>> 18 & 0x3f]);
                os.write(toBase64[part >>> 12 & 0x3f]);
                os.write(toBase64[part >>> 6 & 0x3f]);
                os.write(toBase64[part & 0x3f]);
            }
            if (fis.available() == 2) {
                a = (byte) fis.read();
                b = (byte) fis.read();
                int part = (a & 0xff) << 16
                        | (b & 0xff) << 8;
                os.write(toBase64[part >>> 18 & 0x3f]);
                os.write(toBase64[part >>> 12 & 0x3f]);
                os.write(toBase64[part >>> 6 & 0x3c]);
                os.write('=');
            } else if (fis.available() == 1) {
                a = (byte) fis.read();
                int part = (a & 0xff) << 16;
                os.write(toBase64[part >>> 18 & 0x3f]);
                os.write(toBase64[part >>> 12 & 0x30]);
                os.write('=');
                os.write('=');
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
