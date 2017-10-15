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


        final File fin = new File(finPath);
        final File fout;

        if (foutPath != null) {
            fout = new File(foutPath);
        } else {
            fout = File.createTempFile("based_file_", ".txt");
            fout.deleteOnExit();
        }

        try (
                BufferedInputStream bis = new BufferedInputStream(new FileInputStream(fin));
                BufferedOutputStream fout1 = new BufferedOutputStream(new FileOutputStream(fout));
            ) {

                int numOfBytesRead, num;

                byte[] arr = new byte[3];

                while ((numOfBytesRead = bis.read(arr, 0, 3)) != -1) {
                    if (numOfBytesRead == 3) {
                        num = ((arr[0] & 0xff) << 16) + ((arr[1] & 0xff) << 8) + (arr[2] & 0xff);

                        for (int i = 0; i < 4; i++)
                            fout1.write(toBase64[(num >> 18 - 6 * i) & 0b111111]);
                    } else {
                        for (int i = numOfBytesRead; i < 3; i++)
                            arr[i] = 0;

                        num = ((arr[0] & 0xff) << 16) + ((arr[1] & 0xff) << 8) + (arr[2] & 0xff);

                        for (int i = 0; i < 4; i++) {
                            if (i < numOfBytesRead + 1) fout1.write(toBase64[(num >> 18 - 6 * i) & 0b111111]);
                            else fout1.write('=');
                        }
                    }
                }
                return fout;
            }
        /* XXX: https://docs.oracle.com/javase/8/docs/api/java/io/File.html#deleteOnExit-- */
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
        new Bootstrapper(args, encoder).bootstrap(8000);
    }

}
