package ru.track.io;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ru.track.io.vendor.Bootstrapper;
import ru.track.io.vendor.FileEncoder;

public final class TaskImplementation implements FileEncoder {
    /**
     * Implement FileEncoder via base64.
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
            fout = File.createTempFile("encoded-", "-base64");
            fout.deleteOnExit();
        }

        try (
                final FileInputStream fis = new FileInputStream(fin);
                final FileWriter fos = new FileWriter(fout);
        ) {
            byte[] inBytes = new byte[(int)(fin.length())];
            fis.read(inBytes);
            int remainder = inBytes.length % 3;

            char[] outChars = new char[(inBytes.length + 2) / 3 * 4];

            int outCharsIndex = 0;
            int index = 0;
            for (; index < inBytes.length - 2; index += 3) {
                char[] charsToWrite = new char[4];
                charsToWrite[0] = toBase64[(inBytes[index] >> 2) & 0x3f];
                charsToWrite[1] = toBase64[(inBytes[index] << 4) & 0x30 | ((inBytes[index + 1] >> 4) & 0x0f)];
                charsToWrite[2] = toBase64[(inBytes[index + 1] << 2) & 0x3f | (inBytes[index + 2] >> 6) & 0x03];
                charsToWrite[3] = toBase64[inBytes[index + 2] & 0x3f];
                for (int sliceIndex = 0; sliceIndex < 4; ++sliceIndex) {
                    outChars[outCharsIndex++] = charsToWrite[sliceIndex];
                }
            }

            if (remainder != 0) {
                char[] charsToWrite = new char[4];

                if (remainder == 1) {
                    charsToWrite[0] = toBase64[(inBytes[index] >> 2) & 0x3f];
                    charsToWrite[1] = toBase64[(inBytes[index] << 4) & 0x30];
                    charsToWrite[2] = '=';
                    charsToWrite[3] = '=';
                } else {
                    charsToWrite[0] = toBase64[(inBytes[index] >> 2) & 0x7f];
                    charsToWrite[1] = toBase64[(inBytes[index] << 4) & 0x30 | ((inBytes[index + 1] >> 4) & 0x0f)];
                    charsToWrite[2] = toBase64[(inBytes[index + 1] << 2) & 0x3f];
                    charsToWrite[3] = '=';
                }

                for (int sliceIndex = 0; sliceIndex < 4; ++sliceIndex) {
                    outChars[outCharsIndex++] = charsToWrite[sliceIndex];
                }
            }

            fos.write(outChars);
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
