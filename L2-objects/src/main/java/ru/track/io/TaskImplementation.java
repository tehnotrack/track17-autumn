package ru.track.io;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ru.track.io.vendor.Bootstrapper;
import ru.track.io.vendor.FileEncoder;
import ru.track.io.vendor.ReferenceTaskImplementation;

import java.io.File;

import java.io.*;

public final class TaskImplementation implements FileEncoder {

    private StringBuilder convertThreeBytes(int value, int zeros) {
        StringBuilder res = new StringBuilder();
        for (int i = 3; i >= zeros; --i) {
            res.append(toBase64[(value >> (i * 6)) & 0x3f]);
        }
        for (int i = 0; i < zeros; ++i) {
            res.append("=");
        }
        return res;
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
        InputStream in = new FileInputStream(finPath);

        File fout;
        if (foutPath == null) {
            fout = File.createTempFile(finPath, "x64");
            fout.deleteOnExit();
        } else {
            fout = new File(foutPath);
        }
        StringBuilder result = new StringBuilder();
        byte[] buf = new byte[3];
        int readBytes = in.read(buf);
        while (readBytes > 0) {
            int threeBytes = 0;
            for (int i = 0; i < readBytes; ++i) {
                int c = buf[i] & 0xFF;
                threeBytes += (c << (16 - 8 * i));
            }
            result.append(convertThreeBytes(threeBytes, 3 - readBytes));
            readBytes = in.read(buf);
        }
        in.close();
        OutputStream out = new FileOutputStream(fout);
        out.write(result.toString().getBytes());
        out.close();
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
