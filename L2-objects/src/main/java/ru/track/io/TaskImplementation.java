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

        File fin = new File(finPath);
        File fout;

        if (foutPath == null) {
            fout = File.createTempFile("Base_64", ".txt");
            fout.deleteOnExit();
        }

        else fout = new File(foutPath);

        FileInputStream is = new FileInputStream(fin);

        try(BufferedInputStream bis = new BufferedInputStream(is);FileWriter fw = new FileWriter( fout )){
            byte[] arr = new byte [3];

            int count;

            while((count = bis.read(arr,0,3)) != -1)
            {
                int num = 0;
                for (int i = 0; i < count; i++) {

                    num |= ((arr[i] & 0xff) << (8 * (2 - i)));
                }
                for (int i = 0; i < count + 1; i++) {
                    fw.write(toBase64[63 & (num >> 6 * (3 - i))]);
                }
                if (count == 1) {
                    fw.write('=');
                    fw.write('=');
                }
                if (count == 2) {
                    fw.write('=');
                }
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
