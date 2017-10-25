package ru.track.io;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ru.track.io.vendor.Bootstrapper;
import ru.track.io.vendor.FileEncoder;

import java.io.*;

public final class TaskImplementation implements FileEncoder {

    private static int convertToUnsigned(byte k) {
        return k & 0xff;
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
            fout = File.createTempFile("based_file_", ".base64");
        }
        fout.deleteOnExit();

        try ( InputStream inputStream = new BufferedInputStream(new FileInputStream(finPath));
              OutputStream outputStream = new BufferedOutputStream(new FileOutputStream(fout))) {

            int chunkValue = 0;
            int readBytesNumber = 0;
            int lastReadByte = 0;

            while (lastReadByte != -1) {
                chunkValue = 0;
                readBytesNumber = 3;
                for (int i=0;i<3;i++) {
                    lastReadByte = inputStream.read();
                    chunkValue <<= 8;

                    if (lastReadByte == -1) {
                        readBytesNumber--;
                    } else {
                        chunkValue += lastReadByte;
                    }
                }

                if (readBytesNumber == 0) {
                    break;
                }

                if (readBytesNumber == 3) {
                    for (int i = 3; i >= 0; i--) {
                        outputStream.write(toBase64[chunkValue >> (6 * i)]);
                        chunkValue %= (1 << (6 * i));
                    }
                } else {
                    for (int j = 0; j < readBytesNumber + 1; j++) {
                        outputStream.write(toBase64[chunkValue / (1 << (6 * (3 - j)))]);
                        chunkValue %= (1 << (6 * (3 - j)));
                    }

                    for (int j = 0; j < 3 - readBytesNumber; j++) {
                        outputStream.write('=');
                    }
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
