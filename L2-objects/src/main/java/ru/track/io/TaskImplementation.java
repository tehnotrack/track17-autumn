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
            int index = 0;
            byte[] bytesRead = new byte[3];
            char[] charsToWrite = new char[4];
            for (; index < fin.length() - 2; index += 3) {
                fis.read(bytesRead, 0, 3);
                charsToWrite[0] = toBase64[(bytesRead[0] >> 2) & 0x3f];
                charsToWrite[1] = toBase64[(bytesRead[0] << 4) & 0x30 | ((bytesRead[1] >> 4) & 0x0f)];
                charsToWrite[2] = toBase64[(bytesRead[1] << 2) & 0x3f | (bytesRead[2] >> 6) & 0x03];
                charsToWrite[3] = toBase64[bytesRead[2] & 0x3f];
                fos.write(charsToWrite);
            }

            int remainder = (int) fin.length() % 3;
            if (remainder != 0) {

                if (remainder == 1) {
                    byte zeroByte = (byte) fis.read();
                    charsToWrite[0] = toBase64[(zeroByte >> 2) & 0x3f];
                    charsToWrite[1] = toBase64[(zeroByte << 4) & 0x30];
                    charsToWrite[2] = '=';
                    charsToWrite[3] = '=';
                } else {
                    byte zeroByte = (byte) fis.read();
                    byte firstByte = (byte) fis.read();
                    charsToWrite[0] = toBase64[(zeroByte >> 2) & 0x7f];
                    charsToWrite[1] = toBase64[(zeroByte << 4) & 0x30 | ((firstByte >> 4) & 0x0f)];
                    charsToWrite[2] = toBase64[(firstByte << 2) & 0x3f];
                    charsToWrite[3] = '=';
                }

                fos.write(charsToWrite);
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
