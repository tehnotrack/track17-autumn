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
                    final BufferedOutputStream fos = new BufferedOutputStream(new FileOutputStream(fout));
            ) {
                File retValue = encode(fis, fos, (int)fin.length(), fout.getPath());
                return retValue;
            }
    }

    private static final char[] toBase64 = {
            'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M',
            'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z',
            'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm',
            'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z',
            '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '+', '/'
    };

    private File encode(@NotNull BufferedInputStream fis, @NotNull BufferedOutputStream fos, @NotNull int length, @NotNull String foutPath) throws IOException {
        byte[] arrayOfBytes = new byte[3];
        for (int i = 0; i < length; i += 3) {
            int offset = 0;
            int data3bytes = 0;
            int numberOfBytesRead = fis.read(arrayOfBytes, 0, 3);
            if (numberOfBytesRead != 3 && numberOfBytesRead != -1)
                offset = 3 - numberOfBytesRead;
            for (int j = 0; j < numberOfBytesRead; ++j) {
                data3bytes |= ((arrayOfBytes[j] & 0xFF) << (8 * (2 - j)));
            }
            for (int j = 0; j < 4 - offset; j++) { // дозаписать
                int c = ((data3bytes <<  6*j) & 0xFC0000) >> 18; // FC - старшие 6 единиц
                fos.write(toBase64[c]);
            }
            for (int j = 0; j < offset; j++) {
                fos.write('=');
            }
        }
        return new File(foutPath);
    }


    public static void main(String[] args) throws IOException {
        final FileEncoder encoder = new TaskImplementation();
        // NOTE: open http://localhost:9000/ in your web browser
        new Bootstrapper(args, encoder).bootstrap(9000);
    }

}