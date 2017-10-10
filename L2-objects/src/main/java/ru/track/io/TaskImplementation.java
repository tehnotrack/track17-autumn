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
        /* XXX: https://docs.oracle.com/javase/8/docs/api/java/io/File.html#deleteOnExit-- */

        File fin = new File(finPath);
        File fout;

        if (foutPath == null) {
            fout = File.createTempFile("Base64OutputFile", ".txt");
            fout.deleteOnExit();
        } else {
            fout = new File(foutPath);
        }


        try (InputStream is = new FileInputStream(fin); FileWriter fw = new FileWriter(fout)) {
            while (true) {
                int c0 = is.read();
                if (c0 == -1)
                    break;
                int c1 = is.read();
                int c2 = is.read();

                int block = ((c0 & 0xFF) << 16) | ((Math.max(c1, 0) & 0xFF) << 8) | (Math.max(c2, 0) & 0xFF);

                fw.write(toBase64[block >> 18 & 63]);
                fw.write(toBase64[block >> 12 & 63]);
                fw.write(c1 == -1 ? '=' : toBase64[block >> 6 & 63]);
                fw.write(c2 == -1 ? '=' : toBase64[block & 63]);

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
        final FileEncoder encoder = new TaskImplementation();//ReferenceTaskImplementation();
        // NOTE: open http://localhost:9000/ in your web browser
        new Bootstrapper(args, encoder).bootstrap(9000);
        
    }



}
