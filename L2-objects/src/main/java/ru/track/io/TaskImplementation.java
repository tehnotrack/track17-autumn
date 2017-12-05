package ru.track.io;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ru.track.io.vendor.Bootstrapper;
import ru.track.io.vendor.FileEncoder;
import ru.track.io.vendor.ReferenceTaskImplementation;

import java.io.*;
import java.util.Scanner;

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

        File finFile = new File(finPath);
        File foutFile = new File(foutPath == null ? "./createdFile.txt" : foutPath);

        try (
            BufferedInputStream reader = new BufferedInputStream(new FileInputStream(finPath));
            FileWriter writer = new FileWriter(foutFile);
        ) {
            byte[] byteArray = new byte[3];

            int readed = reader.read(byteArray, 0, 3);
            int i = 0;
            int tmp3Char = 0;

            while (readed >= 0) {
                i = 0;
                do {
                    if (i == readed) {
                        break;
                    }
                    tmp3Char |= (byteArray[i] >= 0 ? byteArray[i] : (255 & byteArray[i])) << (8 * (2 - i));
                    i++;
                } while (i != 3);

                int j = 0;
                while (j != i + 1) {
                    writer.write(toBase64[(tmp3Char & (0x3f << (6 * (3 - j)))) >>> 6 * (3 - j)]);
                    j++;
                }
                j = 0;
                while (j < 3 - readed) {
                    writer.write('=');
                    j++;
                }
                tmp3Char = 0;

                readed = reader.read(byteArray, 0, 3);
            }

            return foutFile;
        }
//        throw new UnsupportedOperationException(); // TODO: implement
    }

    private static final char[] toBase64 = {
            'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M',
            'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z',
            'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm',
            'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z',
            '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '+', '/'
    };

    public static void main(String[] args) throws IOException {
//        final FileEncoder encoder = new ReferenceTaskImplementation();
        final FileEncoder encoder = new TaskImplementation();
        // NOTE: open http://localhost:9000/ in your web browser
//        String[] argc = {"/Users/alexander/Desktop/Track/Java/gitfolder/track17-autumn/L2-objects/image_256.png","/Users/alexander/Desktop/Track/Java/gitfolder/track17-autumn/L2-objects/image_1112.txt"};

        new Bootstrapper(args, encoder).bootstrap(9000);
    }

}
