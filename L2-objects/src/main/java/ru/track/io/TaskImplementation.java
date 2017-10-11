package ru.track.io;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ru.track.io.vendor.Bootstrapper;
import ru.track.io.vendor.FileEncoder;
import ru.track.io.vendor.ReferenceTaskImplementation;

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
        final File fin = new File(finPath);
        final File fout;

        if (foutPath != null) {
            fout = new File(foutPath);
        } else {
            fout = File.createTempFile("based_file_", ".txt");
            fout.deleteOnExit();
        }

        try (BufferedInputStream reader = new BufferedInputStream(new FileInputStream(fin)); FileWriter wrighter = new FileWriter(fout)) {
            byte b1, b2, b3;
            while (true) {
                b1 = (byte) reader.read();
                b2 = (byte) reader.read();
                b3 = (byte) reader.read();

                if (b1 == -1) {
                    break;
                }
                int signs;
                if (b2 == -1) {
                    signs = ((b1 & 0x3F) << 16);
                } else if (b3 == -1) {
                    signs = ((b1 & 0x3F) << 16) | ((b2 & 0x3F) << 8);
                } else {
                    signs = ((b1 & 0x3F) << 16) | ((b2 & 0x3F) << 8) | (b3 & 0x3F);
                }
                wrighter.write(toBase64[signs >> 18 & 63]);
                wrighter.write(toBase64[signs >> 12 & 63]);
                if (b2 == -1 && b3 == -1) {
                    wrighter.write('=');
                    wrighter.write('=');
                } else if (b2 != -1 && b3 == -1) {
                    wrighter.write(toBase64[signs >> 6 & 63]);
                    wrighter.write('=');
                } else {
                    wrighter.write(toBase64[signs >> 6 & 63]);
                    wrighter.write(toBase64[signs & 63]);
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
        new Bootstrapper(args, encoder).bootstrap(9000);
    }

}
