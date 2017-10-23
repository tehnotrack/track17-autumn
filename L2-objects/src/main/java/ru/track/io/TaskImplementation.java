package ru.track.io;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ru.track.io.vendor.Bootstrapper;
import ru.track.io.vendor.FileEncoder;
import ru.track.io.vendor.ReferenceTaskImplementation;

import java.io.File;
import java.io.IOException;
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
        BufferedInputStream input = new BufferedInputStream(new FileInputStream(fin));

        File fout;
        if (foutPath != null) {
            fout = new File(foutPath);
        } else {
            fout = File.createTempFile("based_file_", ".txt");
            fout.deleteOnExit();
        }
        OutputStream out = new FileOutputStream(fout);

        //create buffer of three bytes
        byte[] bufferOfThreeBytes = new byte[3];
        int amountOfReadClips;

        //read data from file by 3 bytes at the time
        while((amountOfReadClips = input.read(bufferOfThreeBytes, 0, 3)) > 0) {
            int clipOfThreeBytes = 0;
            for (int counter = 0; counter < amountOfReadClips; counter++){
                // according to the algorythm, shift iterates 16, 8, 0 in series.
                //therefore we make bitwise OR and shift to the left(multiply by 'shift')
                //0xff=0d255=0b11111111
                int shift = (8 * (2 - counter));
                clipOfThreeBytes = clipOfThreeBytes | (bufferOfThreeBytes[counter] & 0xff) << shift;
            }

            //if amountOfReadClips not equal three, we fill the rest of buffer with '='
            char[] result = new char[]{'=', '=', '=', '='};
            for (int counter = 0; counter < amountOfReadClips + 1; counter++) {
                // according to the algorythm, shift iterates 18, 12, 6, 0 in series.
                //therefore we make  shift to the right(divide by 'shift')
                //and use it as index for search in toBase64 array
                //0x3f=0d63=0b111111
                int shift = 6 * (3 - counter);
                result[counter] = toBase64[clipOfThreeBytes >> shift & 0x3f];
               // out.write(result[counter]);
            }

            out.write(new String(result).getBytes());

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
        // NOTE: open http://loca lhost:9000/ in your web browser
        new Bootstrapper(args, encoder).bootstrap(9000);
    }

}
