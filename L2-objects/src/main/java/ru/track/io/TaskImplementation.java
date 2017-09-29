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

        try (BufferedInputStream input = new BufferedInputStream(new FileInputStream(fin));
             PrintStream output = new PrintStream(new FileOutputStream(fout))) {
            byte[] bytes = new byte[3];
            int numOfBytesRead;
            while ((numOfBytesRead = input.read(bytes, 0, 3)) > 0) {
                int threeBytes = 0;
                for (int i = 0; i < numOfBytesRead; i++) {
                    final int shift = 8 * (2 - i);  // 16, 8, 0
                    threeBytes = threeBytes | (bytes[i] & 0xff) << shift;
                }

                char[] result = new char[]{'=', '=', '=', '='};
                for (int i = 0; i < numOfBytesRead + 1; i++) {
                    final int shift = 6 * (3 - i);  // 18, 12, 6, 0
                    result[i] = toBase64[threeBytes >> shift & 0x3f];
                }
                output.print(result);
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
