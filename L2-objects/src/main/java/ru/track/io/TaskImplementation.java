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
        final FileEncoder encoder = new TaskImplementation();

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
        final File fout;
        if (foutPath != null) {
            fout = new File(foutPath);
        } else {
            fout = File.createTempFile("based_file_", ".txt");
            fout.deleteOnExit();
        }

        try (
                BufferedWriter fw = new BufferedWriter(new FileWriter(fout));
                BufferedInputStream fis = new BufferedInputStream(new FileInputStream(finPath));
        ) {
            byte[] buffer = new byte[8001];
            int nRead = -1;
            while ((nRead = fis.read(buffer, 0, buffer.length)) != -1) {
                int i = 0;
                while (i < (nRead - nRead % 3)) {
                    int threeBytes;
                    threeBytes = (((int) buffer[i] & 0b11111111) << 16)
                            + (((int) buffer[i + 1] & 0b11111111) << 8)
                            + ((int) buffer[i + 2] & 0b11111111);

                    fw.append(toBase64[(threeBytes & 0b111111000000000000000000) >> 18])
                            .append(toBase64[(threeBytes & 0b111111000000000000) >> 12])
                            .append(toBase64[(threeBytes & 0b111111000000) >> 6])
                            .append(toBase64[threeBytes & 0b111111]);

                    i = i + 3;
                }

                if (nRead % 3 == 1) {
                    fw.append(toBase64[((int) buffer[nRead - 1] & 0b11111111) >> 2])
                            .append(toBase64[(buffer[nRead - 1] & 0b11) << 4])
                            .append("==");
                } else if (nRead % 3 == 2) {
                    int twoBytes;
                    twoBytes = (((int) buffer[nRead - 2] & 0b11111111) << 8)
                            + (((int) buffer[nRead - 1] & 0b11111111));


                    fw.append(toBase64[(twoBytes & 0b1111110000000000) >> 10])
                            .append(toBase64[(twoBytes & 0b1111110000) >> 4])
                            .append(toBase64[(twoBytes & 0b1111) << 2])
                            .append("=");
                }

            }

            return fout;
        }

    }

}