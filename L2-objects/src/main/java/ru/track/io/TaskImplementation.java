package ru.track.io;

import org.apache.commons.io.IOUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ru.track.io.vendor.Bootstrapper;
import ru.track.io.vendor.FileEncoder;
import ru.track.io.vendor.ReferenceTaskImplementation;

import java.io.*;
import java.util.Arrays;

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

        try(
                final BufferedInputStream ifStream = new BufferedInputStream(new FileInputStream(fin));
                final BufferedOutputStream ofStream = new BufferedOutputStream(new FileOutputStream(fout))
        ) {


            byte[] data = new byte[3];

            int dataRead = 0;

            while (ifStream.available() > 0) {
                Arrays.fill(data, (byte) 0);

                dataRead = ifStream.read(data, 0, 3);

                int buffer = ((data[0] & 0xff) << 16) + ((data[1] & 0xff) << 8) + ((data[2] & 0xff));
                ofStream.write(this.toBase64[((buffer >> 18) & 0x3f)]);
                ofStream.write(this.toBase64[((buffer >> 12) & 0x3f)]);
                if (dataRead % 3 == 1) {
                    ofStream.write((byte) '=');
                } else {
                    ofStream.write(this.toBase64[((buffer >> 6) & 0x3f)]);
                }
                if (dataRead % 3 == 1 || dataRead % 3 == 2) {
                    ofStream.write((byte) '=');
                } else {
                    ofStream.write(this.toBase64[((buffer >> 0) & 0x3f)]);
                }
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
