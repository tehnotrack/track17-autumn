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
        final File fin = new File(finPath);
        final File fout;

        if (foutPath != null) {
            fout = new File(foutPath);
        } else {
            fout = File.createTempFile("based_file_", ".txt");
            fout.deleteOnExit();
        }

        int count;
        FileInputStream fis = null;
        FileOutputStream os = null;
        try {
            fis = new FileInputStream(fin);
            os = new FileOutputStream(fout);
            byte[] data = new byte[3];

            while ((count = fis.read(data)) != -1) {
                os.write(convert3bytes(data, count));
            }

        } finally {
            if (fis != null) {
                   fis.close();
            }
        }


        return fout;
    }

    private static byte[] convert3bytes(byte[] buf, int count) {
        int bytesTogether;
        if (count == 3)
            bytesTogether = buf[2] + (buf[1] << 8) + (buf[0] << 16);
        else if(count == 2)
            bytesTogether = (buf[1] << 8) + (buf[0] << 16);
        else if(count == 1)
            bytesTogether = buf[0] << 16;

        for (int i = 3; i >= zeros; --i) {
            res.append(toBase64[(value >> (i * 6)) & 0x3f]);
        }
        for (int i = 0; i < zeros; ++i) {
            res.append("=");
        }
        (value >> (i * 6)) & 0x3f]

        return buf;
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
