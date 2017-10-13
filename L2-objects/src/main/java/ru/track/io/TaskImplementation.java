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
                final BufferedInputStream fis = new BufferedInputStream(new FileInputStream(fin));
                final BufferedOutputStream os = new BufferedOutputStream(new FileOutputStream(fout));
        )
        {
            byte[] threeBytes = new byte[3];
            int nBytesRead = fis.read(threeBytes, 0, 3);

            while (nBytesRead > 0)
            {
                // запишем все три байта в одну переменную
                int allBytes = 0;
                for (int i = 0; i < nBytesRead; i++)
                {
                    allBytes = (allBytes << 8) | (threeBytes[i] & 0xff);
                }
                // добьем до трех байт
                for (int i = 0; i < 3 - nBytesRead; i++)
                {
                    allBytes = allBytes << 8;
                }

                // переведем три байта в четыре символа в кодировке base64
                // 1 байт -> 2 символа, 2 -> 3, 3 -> 4
                for (int i = 0; i < nBytesRead + 1; i++)
                {
                    os.write(toBase64[(allBytes >> (6 * (3 - i))) & 0b111111]);
                }

                // добиваем концовку до трех байт
                if (nBytesRead <= 2)
                {
                    os.write('=');
                    if (nBytesRead <= 1)
                    {
                        os.write('=');
                    }
                }

                nBytesRead = fis.read(threeBytes, 0, 3);
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
