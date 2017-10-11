package ru.track.io;

import org.apache.commons.io.IOUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ru.track.io.vendor.Bootstrapper;
import ru.track.io.vendor.FileEncoder;
import ru.track.io.vendor.ReferenceTaskImplementation;

import java.io.*;
import java.util.Base64;

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

        if (foutPath != null)
        {
            fout = new File(foutPath);
        }
        else
        {
            fout = File.createTempFile("based_file_", ".txt");
            fout.deleteOnExit();
        }

        try (
                final FileInputStream fis = new FileInputStream(fin);
                final OutputStream os = new FileOutputStream(fout);
        )
        {
            StringBuilder str = new StringBuilder();    // итоговая строка в base64
            byte[] three_bytes = new byte[3];
            int n_bytes_red = fis.read(three_bytes, 0, 3);

            while (n_bytes_red > 0)
            {
                // запишем все три байта в одну переменную
                int all_bytes = three_bytes[0];
                for (int i = 1; i < n_bytes_red; i++)
                {
                    all_bytes = (all_bytes << 8) | three_bytes[i];
                }
                // добьем до трех байт
                for (int i = 0; i < 3 - n_bytes_red; i++)
                {
                    all_bytes = all_bytes << 8;
                }

                // переведем три байта в четыре символа в кодировке base64
                // 1 байт -> 2 символа, 2 -> 3, 3 -> 4
                for (int i = 0; i < n_bytes_red + 1; i++)
                {
                    str.append(toBase64[(all_bytes >> (6 * (3 - i))) & 0b111111]);
                }

                // добиваем концовку до трех байт
                if (n_bytes_red <= 2)
                {
                    str.append('=');
                    if (n_bytes_red <= 1)
                    {
                        str.append('=');
                    }
                }

                n_bytes_red = fis.read(three_bytes, 0, 3);
            }
            os.write(str.toString().getBytes());
            //System.out.println(str.toString());
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
