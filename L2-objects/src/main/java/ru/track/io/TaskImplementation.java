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
        try(FileInputStream fis = new FileInputStream(fin);
            FileOutputStream os = new FileOutputStream(fout);
            BufferedOutputStream bos = new BufferedOutputStream(os)){
            byte[] in = new byte[3];
            char[] out = new char[4];
            String str;
            int n, i;
            while((n = fis.read(in)) > 0){
                for(i = n; i < in.length; i++)
                    in[i] = 0;
                out[0] = toBase64[63 & (in[0] >>> 2)];
                out[1] = toBase64[63 & (((in[1] >>> 4) & 15) | in[0] << 4)];
                out[2] = out[3] = '=';
                if(n > 1) {
                    out[2] = toBase64[63 & (((in[2] >>> 6) & 3) | in[1] << 2)];
                    if(n > 2)
                        out[3] = toBase64[in[2] & 63];
                }
                bos.write(new String(out).getBytes("UTF-8"));
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
