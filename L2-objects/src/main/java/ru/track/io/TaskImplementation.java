package ru.track.io;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ru.track.io.vendor.Bootstrapper;
import ru.track.io.vendor.FileEncoder;

import java.io.*;
import java.util.BitSet;

public final class TaskImplementation implements FileEncoder {

    private FileEncoder encoder;

    /**
     * @param finPath  where to read binary data from
     * @param foutPath where to write encoded data. if null, please create and use temporary file.
     * @return file to read encoded data from
     * @throws IOException is case of input/output errors
     */
    @NotNull
    public File encodeFile(@NotNull String finPath, @Nullable String foutPath) throws IOException {
        /* XXX: https://docs.oracle.com/javase/8/docs/api/java/io/File.html#deleteOnExit--
        throw new UnsupportedOperationException(); // TODO: implement*/
        File fin = new File(finPath); // file with binary data
        File fout; // file with encoded data to be returned
        if (foutPath == null) {
            fout = File.createTempFile("my_temp_output", ".txt");
            fout.deleteOnExit();
        } else {
            fout = new File(foutPath);
        }
        StringBuilder encoded = new StringBuilder(); // string to put into fout
        FileInputStream reader = new FileInputStream(fin);
        PrintWriter pw = new PrintWriter(fout.getAbsoluteFile()); // writer to write fout
        if (!fout.exists()) { // create the file if it doesn't exist
            fout.createNewFile();
        }
        // encoding in while ()
        while(true) {
            //checking the end of file
            int firstByte = reader.read();
            if (firstByte == -1) break;
            //filling buffer
            BitSet buffer = new BitSet(24);
            int[] bytes24 = {firstByte, reader.read(), reader.read()};
            int encodedBytes = 3;
            for (int i = 0; i < 3; i++) {
                if (bytes24[i] == -1) {
                    bytes24[i] = 0;
                    encodedBytes -= 1;
                }
            }
            for (int i = 0; i < 3; i++) {
                for (int j = 7; j >= 0; j --) {
                    int value = (bytes24[i] >> j) % 2;
                    buffer.set(i * 8 + (7 - j), (value == 1) ? true : false);
                }
            }
            //encoding
            for (int i = 0; i < 4; i++) {
                BitSet base = buffer.get(i * 6, (i + 1) * 6);
                int value = 0;
                for (int j = 5; j >= 0; j--) value += (base.get(j))? 1 << (5 - j) : 0;
                encoded.append((encodedBytes - i >= 0)? toBase64[value]: '=');
            }
        }
        try { // try-finally block is put for inevitable closure of writer
            pw.write(encoded.toString());
        } finally {
            pw.close();
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
