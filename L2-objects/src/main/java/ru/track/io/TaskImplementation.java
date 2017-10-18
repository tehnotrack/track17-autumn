package ru.track.io;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ru.track.io.vendor.Bootstrapper;
import ru.track.io.vendor.FileEncoder;

import java.io.*;


public final class TaskImplementation implements FileEncoder {


    private byte[] encodeTriplet(byte[] t, int count) {
        int triplet;
        byte[] res = new byte[4];
        if (count == 3)
            triplet = (t[2] & 0xff) + ((t[1]& 0xff) << 8) + ((t[0]& 0xff) << 16);
        else if(count == 2)
            triplet = ((t[1]& 0xff) << 8) + ((t[0]& 0xff) << 16);
        else
            triplet = (t[0]& 0xff) << 16;

        for (int i = 0; i < 4; i++) {
            res[i] = (byte) toBase64[(triplet >>> (6 * (3 - i))) & 0x3f];
        }
        if (count == 1) {
            res[2] = res[3] = (byte)'=';
        } else if (count == 2) {
            res[3] = (byte)'=';
        }
        return res;
    }

    /**
     * @param finPath  where to read binary data from
     * @param foutPath where to write encoded data. if null, please create and use temporary file.
     * @return file to read encoded data from
     * @throws IOException is case of input/output errors
     */
    @NotNull
    public File encodeFile(@NotNull String finPath, @Nullable String foutPath) throws IOException {
        File fin = new File(finPath);
        File fout;

        if (foutPath == null) {
            fout = File.createTempFile("Base64", ".tmp");
            fout.deleteOnExit();
        } else fout = new File(foutPath);

        try (InputStream bis = new BufferedInputStream(new FileInputStream(fin));
             OutputStream bos = new BufferedOutputStream(new FileOutputStream(fout))) {

            byte[] triplet = new byte[3];
            int count;
            while ((count = bis.read(triplet)) != -1) {
                bos.write(encodeTriplet(triplet, count));
            }
            return fout;
        }

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
