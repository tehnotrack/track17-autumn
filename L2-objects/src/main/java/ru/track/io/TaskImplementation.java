package ru.track.io;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ru.track.io.vendor.Bootstrapper;
import ru.track.io.vendor.FileEncoder;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.HashSet;

public final class TaskImplementation implements FileEncoder {

    private static int convertToUnsigned(byte k) {
        return k & 0xff;
    }
    private HashMap<String, String> encodedFiles = new HashMap<>(); // maps input to output fnames

    /**
     * @param finPath  where to read binary data from
     * @param foutPath where to write encoded data. if null, please create and use temporary file.
     * @return file to read encoded data from
     * @throws IOException is case of input/output errors
     */
    @NotNull
    public File encodeFile(@NotNull String finPath, @Nullable String foutPath) throws IOException {
        final File fout;

        if (encodedFiles.containsKey(finPath)) {
            fout = new File(encodedFiles.get(finPath));
            return fout;
        }

        if (foutPath != null) {
            fout = new File(foutPath);
        } else {
            fout = File.createTempFile("based_file_", ".base64");
        }
        fout.deleteOnExit();

        final byte[] data = Files.readAllBytes(Paths.get(finPath));
        final StringBuilder builder = new StringBuilder();
        final int maxK = (int) Math.pow(64, 3);
        int chunkValue;
        int k;

        for(int tripletNumber = 0; tripletNumber < data.length / 3; tripletNumber++) {
            chunkValue =convertToUnsigned(data[tripletNumber * 3]) * 65536 +
                        convertToUnsigned(data[tripletNumber * 3 + 1]) * 256 +
                        convertToUnsigned(data[tripletNumber * 3 + 2]);
            k = maxK;
            for (int quadrupletNumber =  0; quadrupletNumber < 4; quadrupletNumber++) {
                builder.append(toBase64[chunkValue / k]);
                chunkValue %= k;
                k /= 64;
            }
        }

        chunkValue = 0;
        int last = (data.length / 3) * 3;
        k = 65536;
        for (int i=0; i < 3; i++) {
            if (last + i < data.length) {
                chunkValue += k * convertToUnsigned(data[last + i]);
                k /= 256;
            }
        }

        k = maxK;
        if (data.length != last) {
            for (int j = 0; j < (data.length - last + 1); j++) {
                builder.append(toBase64[chunkValue / k]);
                chunkValue %= k;
                k /= 64;
            }

            for (int j = 0; j < 4 - (data.length - last + 1); j++) {
                builder.append("=");
            }
        }


        String res = builder.toString();
        Files.write(fout.toPath(), res.getBytes());
        encodedFiles.put(finPath, fout.getPath());


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
