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

        StringBuilder data = new StringBuilder();
        File out_file;

        if (foutPath == null) {
            out_file = File.createTempFile("EncodedFile", ".txt");
            out_file.deleteOnExit();
        } else {
            out_file = new File(foutPath);
        }

        try (BufferedReader br = new BufferedReader(new FileReader(finPath))) {
            // reading byte by byte and adding the result to string
            int b;
            while ((b = br.read()) != -1) {
                data.append((char) b);
            }
        } catch (IOException e) {
            System.out.print(e.getMessage());
        }

        String r = "", p = "";
        int c = data.length() % 3;

        if (c > 0) {
            for (; c < 3; c++) {
                p += "=";
                data.append("\0");
            }
        }

        for (c = 0; c < data.length(); c += 3) {

            // we add newlines after every 76 output characters, according to
            // the MIME specs
            if (c > 0 && (c / 3 * 4) % 76 == 0)
                r += "\r\n";

            // these three 8-bit (ASCII) characters become one 24-bit number
            int n = (data.charAt(c) << 16) + (data.charAt(c + 1) << 8)
                    + (data.charAt(c + 2));

            // this 24-bit number gets separated into four 6-bit numbers
            int n1 = (n >> 18) & 63, n2 = (n >> 12) & 63, n3 = (n >> 6) & 63, n4 = n & 63;

            // those four 6-bit numbers are used as indices into the base64
            // character list
            r += "" + toBase64[n1] + toBase64[n2]
                    + toBase64[n3] + toBase64[n4];
        }
        PrintWriter pw = new PrintWriter(out_file);
        pw.write(r.substring(0, r.length() - p.length()) + p);
        pw.close();

        return out_file;
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
