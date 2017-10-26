package ru.track.io;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ru.track.io.vendor.Bootstrapper;
import ru.track.io.vendor.FileEncoder;
import ru.track.io.vendor.ReferenceTaskImplementation;

import java.io.*;
import java.util.ArrayList;

public final class TaskImplementation implements FileEncoder {

    private static final char[] toBase64 = {
            'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M',
            'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z',
            'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm',
            'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z',
            '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '+', '/'
    };

    public static void main(String[] args) throws IOException {
        TaskImplementation t = new TaskImplementation();
        //final FileEncoder encoder = new ReferenceTaskImplementation();
        final FileEncoder encoder = new TaskImplementation();
        //t.encodeFile("image_256.png", null);

        // NOTE: open http://localhost:9000/ in your web browser
        new Bootstrapper(args, encoder).bootstrap(9000);
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
        final File fout;
        if (foutPath != null) {
            fout = new File(foutPath);
        } else {
            fout = File.createTempFile("based_file_", ".txt");
            fout.deleteOnExit();
        }

        try (
                FileWriter fw = new FileWriter(fout);
                FileInputStream fis = new FileInputStream(finPath);
        ) {
            byte[] buffer = new byte[1002];
            StringBuilder sb = new StringBuilder();
            int nRead = -1;
            String toWrite;
            while ((nRead = fis.read(buffer)) != -1) {
                int i = 0;
                while (i < (nRead - nRead % 3)) {
                    int threeBytes;
                    threeBytes = (((int) buffer[i] & 0b11111111) << 16)
                            + (((int) buffer[i + 1] & 0b11111111) << 8)
                            + ((int) buffer[i + 2] & 0b11111111);

                    toWrite = "" + toBase64[(threeBytes & 0b111111000000000000000000) >> 18]
                            + toBase64[(threeBytes & 0b111111000000000000) >> 12]
                            + toBase64[(threeBytes & 0b111111000000) >> 6]
                            + toBase64[threeBytes & 0b111111];

                    fw.write(toWrite);
                    i = i + 3;
                }

                if (nRead % 3 == 1) {
                    toWrite = "" + toBase64[((int) buffer[nRead - 1] & 0b11111111) >> 2]
                            + toBase64[(buffer[nRead - 1] & 0b11) << 4]
                            + "==";
                } else if (nRead % 3 == 2) {
                    int twoBytes;
                    twoBytes = (((int) buffer[nRead - 2] & 0b11111111) << 8)
                            + (((int) buffer[nRead - 1] & 0b11111111));

                    toWrite = "" + toBase64[(twoBytes & 0b1111110000000000) >> 10]
                            + toBase64[(twoBytes & 0b1111110000) >> 4]
                            + toBase64[(twoBytes & 0b1111) << 2]
                            + "=";
                } else
                    toWrite = "";

                fw.write(toWrite);
            }

            return fout;
        }

    }

}