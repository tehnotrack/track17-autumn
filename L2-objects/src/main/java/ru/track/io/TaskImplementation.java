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
            fout = File.createTempFile("Base64EncodedFile", ".txt");
            fout.deleteOnExit();
        }

        try (BufferedInputStream bufferedInputStream = new BufferedInputStream(new FileInputStream(fin));
             BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(new FileOutputStream(fout))) {

            byte[] bytes = new byte[3];
            int numberOfReadedBytes = 0;
            int pad = 0;

            while ((numberOfReadedBytes = bufferedInputStream.read(bytes, 0, 3)) > 0){

                if (numberOfReadedBytes == 3){
                    pad = ((bytes[0] & 0xFF) << 16) | ((bytes[1] & 0xFF) << 8) | (bytes[2] & 0xFF);
                    bufferedOutputStream.write(toBase64[0x3f & (pad >> 18)]);
                    bufferedOutputStream.write(toBase64[0x3f & (pad >> 12)]);
                    bufferedOutputStream.write(toBase64[0x3f & (pad >> 6)]);
                    bufferedOutputStream.write(toBase64[0x3f & (pad >> 0)]);
                }

                if (numberOfReadedBytes == 2){
                    pad = ((bytes[0] & 0xFF) << 16) | ((bytes[1] & 0xFF) << 8);
                    bufferedOutputStream.write(toBase64[0x3f & (pad >> 18)]);
                    bufferedOutputStream.write(toBase64[0x3f & (pad >> 12)]);
                    bufferedOutputStream.write(toBase64[0x3f & (pad >> 6)]);
                    bufferedOutputStream.write('=');
                }

                if (numberOfReadedBytes == 1){
                    pad = ((bytes[0] & 0xFF) << 16);
                    bufferedOutputStream.write(toBase64[0x3f & (pad >> 18)]);
                    bufferedOutputStream.write(toBase64[0x3f & (pad >> 12)]);
                    bufferedOutputStream.write('=');
                    bufferedOutputStream.write('=');
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
