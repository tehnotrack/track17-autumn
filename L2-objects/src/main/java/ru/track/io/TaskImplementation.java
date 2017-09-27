package ru.track.io;

import org.apache.commons.io.IOUtils;
import org.jetbrains.annotations.Contract;
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
    @Contract("_, _ -> fail")
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
                final FileInputStream fis = new FileInputStream(fin);
                final OutputStream os = new FileOutputStream(fout);
        ) {
            byte[] input = IOUtils.toByteArray(fis);
            StringBuilder output = new StringBuilder();
            int lengthOfChunk = 3;
            int lengthWithoutLastChunk = input.length / lengthOfChunk * lengthOfChunk;
            for(int i = 0; i < lengthWithoutLastChunk ; i+=3) {
                int b = (input[i] & 0xff) << 16 |
                        (input[i + 1] & 0xff) << 8 |
                        (input[i + 2] & 0xff);
                output.append(toBase64[b >>> 18 & 0x3f]);
                output.append(toBase64[b >>> 12 & 0x3f]);
                output.append(toBase64[b >>> 6 & 0x3f]);
                output.append(toBase64[b & 0x3f]);
            }
            if ((input.length - lengthWithoutLastChunk) == 2) {
                int b = (input[lengthWithoutLastChunk] & 0xff) << 16 |
                        (input[lengthWithoutLastChunk + 1] & 0xff) << 8;
                output.append(toBase64[b >>> 18 & 0x3f]);
                output.append(toBase64[b >>> 12 & 0x3f]);
                output.append(toBase64[b >>> 6 & 0x3c]);
                output.append('=');
            }
            else if ((input.length - lengthWithoutLastChunk) == 1) {
                int b = (input[lengthWithoutLastChunk] & 0xff) << 16;
                output.append(toBase64[b >>> 18 & 0x3f]);
                output.append(toBase64[b >>> 12 & 0x30]);
                output.append('=');
                output.append('=');
            }
            os.write(output.toString().getBytes());
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
