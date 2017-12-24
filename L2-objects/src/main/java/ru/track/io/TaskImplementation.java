package ru.track.io;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ru.track.io.vendor.Bootstrapper;
import ru.track.io.vendor.FileEncoder;
import ru.track.io.vendor.ReferenceTaskImplementation;

import java.io.*;

public final class TaskImplementation implements FileEncoder {
    //encode 3-element 8-bit array with base64 algorithm to 6-bit 4-element array
    public static byte[] translateBytes(byte in[], int size) {
        //6-bit symbols array
        byte result[] = new byte[4];

        if (size == 1) {
            result[0] = (byte) toBase64[(in[0] & 0xFC) >> 2];
            result[1] = (byte) toBase64[((in[0] & 0x03) << 4) | ((in[1] & 0xF0) >> 4)];
            result[2] = (byte) '=';
            result[3] = (byte) '=';
        }
        else if (size == 2) {
            result[0] = (byte) toBase64[(in[0] & 0xFC) >> 2];
            result[1] = (byte) toBase64[((in[0] & 0x03) << 4) | ((in[1] & 0xF0) >> 4)];
            result[2] = (byte) toBase64[((in[2] & 0xC0) >> 6) | ((in[1] & 0x0F) << 2)];
            result[3] = (byte) '=';
        }
        else if (size == 3) {
            result[0] = (byte) toBase64[(in[0] & 0xFC) >> 2];
            result[1] = (byte) toBase64[((in[0] & 0x03) << 4) | ((in[1] & 0xF0) >> 4)];
            result[2] = (byte) toBase64[((in[2] & 0xC0) >> 6) | ((in[1] & 0x0F) << 2)];
            result[3] = (byte) toBase64[in[2] & 0x3F];
        }

        return result;
    }
    /**
     * @param finPath  where to read binary data from
     * @param foutPath where to write encoded data. if null, please create and use temporary file.
     * @return file to read encoded data from
     * @throws IOException is case of input/output errors
     */
    @NotNull
    public File encodeFile(@NotNull String finPath, @Nullable String foutPath) throws IOException {
        /* XXX: https://docs.oracle.com/javase/8/docs/api/java/io/File.html#deleteOnExit-- */

        String fileName;
        if (foutPath != null) {
            fileName = foutPath;
        } else {
            fileName = "based_file_" + ".txt";
        }
        File outFile = new File(fileName);
        outFile.deleteOnExit();
        try (BufferedInputStream in = new BufferedInputStream(new FileInputStream(finPath));
             BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(outFile));
        )
        {
            int c, i=0;
            byte arr[] = new byte[3];

            while ((c = in.read()) != -1) {
                arr[i] = (byte)c;

                i++;
                if (i > 2) { //перекодировать байты в выходной массив
                    out.write(translateBytes(arr, i));
                    i=0;
                    arr[0]=0;
                    arr[1]=0;
                    arr[2]=0;
                }
            }
            if (i > 0)
                out.write(translateBytes(arr, i));
        } catch (IOException e) {}

        return outFile;
    }

    private static final char[] toBase64 = {
            'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M',
            'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z',
            'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm',
            'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z',
            '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '+', '/'
    };

    public static void main(String[] args) throws IOException {
        final FileEncoder encoder = new ReferenceTaskImplementation();
        // NOTE: open http://localhost:9000/ in your web browser
        new Bootstrapper(args, encoder).bootstrap(9000);
    }

}
