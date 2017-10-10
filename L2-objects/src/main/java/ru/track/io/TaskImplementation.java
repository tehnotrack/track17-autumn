package ru.track.io;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ru.track.io.vendor.Bootstrapper;
import ru.track.io.vendor.FileEncoder;
import ru.track.io.vendor.ReferenceTaskImplementation;

import java.io.*;
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

        InputStream inputStream = new FileInputStream(fin);
        StringBuilder sb = new StringBuilder();

        while (true) {
            int char1 = inputStream.read();
            int char2 = inputStream.read();
            int char3 = inputStream.read();

            if (char1 == -1)
                break;

            int tempBuff = ((char1 & 0xFF) << 16) |
                    ((Math.max(char2, 0) & 0xFF) << 8) |
                    ((Math.max(char3, 0) & 0xFF));

            sb.append(toBase64[tempBuff >> 18 & 0x3F]);
            sb.append(toBase64[tempBuff >> 12 & 0x3F]);
            sb.append(char2 == -1 ? '=' : toBase64[tempBuff >> 6 & 0x3F]);
            sb.append(char3 == -1 ? '=' : toBase64[tempBuff & 0x3F]);
        }

        File fout;
        if (foutPath == null) {
            fout = File.createTempFile("TempFile", ".txt");
            fout.deleteOnExit();
        } else {
            fout = new File(foutPath);
        }

        try (PrintWriter pw = new PrintWriter(fout)) {
            pw.write(sb.toString());
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
