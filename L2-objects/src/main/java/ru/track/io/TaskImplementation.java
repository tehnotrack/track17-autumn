package ru.track.io;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ru.track.io.vendor.Bootstrapper;
import ru.track.io.vendor.FileEncoder;
import ru.track.io.vendor.ReferenceTaskImplementation;
import java.io.File;

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
        InputStream in = new FileInputStream(finPath);

        File fout;
        if( foutPath == null) {
            fout = File.createTempFile(finPath, "x64");
            fout.deleteOnExit();
        }
        else
            fout = new File(foutPath);
        OutputStream out = new FileOutputStream(fout);
        int data = in.read();
        while (data != -1){
            data = (data << 16);
            int second = in.read();
            if(second != -1){
                data += second << 8;
            }
            else {
                out.write(toBase64[(data >> 18) & 63]);
                out.write(toBase64[(data >> 12) & 63]);
                out.write('=');
                out.write('=');
                break;
            }
            int last = in.read();
            if(last != -1)
                data += last;
            else {
                out.write(toBase64[(data >> 18) & 63]);
                out.write(toBase64[(data >> 12) & 63]);
                out.write(toBase64[(data >> 6) & 63]);
                out.write('=');
                break;
            }
            out.write(toBase64[(data >> 18) & 63]);
            out.write(toBase64[(data >> 12) & 63]);
            out.write(toBase64[(data >> 6) & 63]);
            out.write(toBase64[data & 63]);
            data = in.read();
        }
        in.close();
        out.close();
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
