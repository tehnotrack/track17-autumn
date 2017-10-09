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

        FileInputStream fis = new FileInputStream(finPath);
        File outFile;

        if (foutPath == null) {
            outFile = File.createTempFile("EncodedFile", ".txt");
            outFile.deleteOnExit();
        } else {
            outFile = new File(foutPath);
        }


        try (BufferedInputStream bis = new BufferedInputStream(fis); FileWriter fw = new FileWriter(outFile)) {

            byte[] data = new byte[3];
            int counter = 0;

            while ((counter = bis.read(data)) != -1) {
                //byte[] output = new byte[4];
                String r = "", p = "";
                if (counter == 3) {
                    // these three 8-bit (ASCII) characters become one 24-bit number
                    int n = (data[0] << 16) + (data[1] << 8) + (data[2]);
                    // this 24-bit number gets separated into four 6-bit numbers
                    int n1 = (n >> 18) & 63, n2 = (n >> 12) & 63, n3 = (n >> 6) & 63, n4 = n & 63;
                    r += "" + toBase64[n1] + toBase64[n2]
                            + toBase64[n3] + toBase64[n4];
                }
                if (counter < 3) {
                    for (int i = counter; i >= counter; i--)
                        data[i] = 0;
                    int n = (data[0] << 16) + (data[1] << 8) + (data[2]);
                    for (int i = 0; i <= counter; i++) {
                        int ni = (n >> 18 - 6 * i) & 63;
                        r += "" + toBase64[ni];
                    }
                    for (; counter < 3; counter++)
                        p += "=";
                    r += p;
                }
                fw.write(r);

            }

        }
        return outFile;
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
