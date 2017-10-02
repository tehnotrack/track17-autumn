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
        //throw new UnsupportedOperationException(); // TODO: implement

        final char[] alpha = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/".toCharArray();

        File fin = new File(finPath);
        File fout;

        if (foutPath == null) {
            fout = File.createTempFile("Base64OutputFile", ".txt");
            fout.deleteOnExit();
        } else {
            fout = new File(foutPath);
        }

        InputStream is = new FileInputStream(fin);
        BufferedReader buf = new BufferedReader(new InputStreamReader(is));

        StringBuilder sb = new StringBuilder();
        int blocks = 0;

        while (true) {
            int c0 = is.read();
            if (c0 == -1)
                break;
            int c1 = is.read();
            int c2 = is.read();

            int block = ((c0 & 0xFF) << 16) | ((Math.max(c1, 0) & 0xFF) << 8) | (Math.max(c2, 0) & 0xFF);

            sb.append(alpha[block >> 18 & 63]);
            sb.append(alpha[block >> 12 & 63]);
            sb.append(c1 == -1 ? '=' : alpha[block >> 6 & 63]);
            sb.append(c2 == -1 ? '=' : alpha[block & 63]);

        }


        PrintWriter pw = new PrintWriter( fout );
        pw.write(sb.toString());
        pw.close();

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
