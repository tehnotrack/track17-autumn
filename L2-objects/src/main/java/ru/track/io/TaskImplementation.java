package ru.track.io;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ru.track.io.vendor.Bootstrapper;
import ru.track.io.vendor.FileEncoder;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;

public final class TaskImplementation implements FileEncoder {

    int convertToUnsigned(byte k) {
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
        /* XXX: https://docs.oracle.com/javase/8/docs/api/java/io/File.html#deleteOnExit-- */
        // throw new UnsupportedOperationException(); // TODO: implement
        final File fout;

        if (foutPath != null) {
            fout = new File(foutPath);
        } else {
            fout = File.createTempFile("based_file_", ".base64");
            fout.deleteOnExit();
        }

        byte[] data = Files.readAllBytes(Paths.get(finPath));
        int n;
        int k;
        StringBuilder builder = new StringBuilder();
        for(int i = 0; i < data.length / 3; i++) {
            n =     convertToUnsigned(data[i * 3]) * 65536 +
                    convertToUnsigned(data[i * 3 + 1]) * 256 +
                    convertToUnsigned(data[i * 3 + 2]);
            k = 262144;
            for (int j=0;j<4;j++){
                builder.append(toBase64[n / k]);
                n %= k;
                k /= 64;
            }
        }

        n = 0;
        int last = (data.length / 3) * 3;
        k = 65536;
        for (int i=0; i < 3; i++) {
            if (last + i < data.length) {
                n += k * convertToUnsigned(data[last + i]);
                k /= 256;
            }
        }
        k = 262144;


        if (data.length != last) {
            for (int j = 0; j < (data.length - last + 1); j++) {
                builder.append(toBase64[n / k]);
                n %= k;
                k /= 64;
            }

            for (int j = 0; j < 4 - (data.length - last + 1); j++) {
                builder.append("=");
            }
        }


        String res = builder.toString();
        Files.write(fout.toPath(), res.getBytes());

        final int L = 64;
        for (int i=0;i<res.length() / L; i++) {
            System.out.println(res.substring(i * L, i * L + L));
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
