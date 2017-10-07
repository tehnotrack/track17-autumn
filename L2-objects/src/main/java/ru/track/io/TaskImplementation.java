package ru.track.io;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ru.track.io.vendor.Bootstrapper;
import ru.track.io.vendor.FileEncoder;
import ru.track.io.vendor.ReferenceTaskImplementation;

import java.io.*;
import java.nio.charset.StandardCharsets;

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
        InputStreamReader reader = new InputStreamReader(new FileInputStream(fin));
        PrintWriter out = new PrintWriter(fout.getAbsoluteFile());

        StringBuilder add = new StringBuilder();

        int [] mas = new int [3];
        while(true) {
            StringBuilder res = new StringBuilder();
            for (int i = 0; i < 3; i++)
                mas[i] = reader.read();


            if (mas[0] == -1)
                break;

            for (int i = 1; i < 3; i++)
                if (mas[i] == -1)
                {
                    mas[i] = Integer.parseInt("0");
                    add.append('=');
                }

            int n = 0;
            for (int j = 0; j < 3; j++)
            {
                n += mas[j] << (16 - 8 * j);
            }

            for (int j = 3; j > -1; j--)
            {
                res.append(toBase64[ (n >> (6 * j)) & 63 ]);
            }
            res.setLength(res.length()-add.length());
            res.append(add);
            out.print(res.toString());


        }

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
