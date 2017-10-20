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

        InputStream inputStream = new FileInputStream(finPath);
        OutputStream outputStream = new FileOutputStream(fout);

        final int MAX_K = (int) Math.pow(64, 3);
        int chunkValue;
        int k;

        while(inputStream.available() > 2) {
            chunkValue =inputStream.read() * 65536 +
                    inputStream.read() * 256 +
                    inputStream.read();
            k = MAX_K;
            for (int quadrupletNumber =  0; quadrupletNumber < 4; quadrupletNumber++) {
                outputStream.write(toBase64[chunkValue / k]);
                chunkValue %= k;
                k /= 64;
            }
        } // no more than 3 bytes are unprocessed now

        chunkValue = 0;
        k = 65536;
        int strayBytes = 0;
        for (int i=0; i < 3; i++) {
            if (inputStream.available() > 0) {
                chunkValue += k * inputStream.read();
                k /= 256;
                strayBytes++;
            }
        } // now the remaining bytes are saved into chunkValue

        k = MAX_K;
        if (strayBytes != 0) {
            for (int j = 0; j < strayBytes; j++) {
                outputStream.write(toBase64[chunkValue / k]);
                chunkValue %= k;
                k /= 64;
            }

            for (int j = 0; j < 4 - strayBytes; j++) {
                outputStream.write('=');
            }
        }


        inputStream.close();
        outputStream.close();


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
