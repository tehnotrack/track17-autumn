package ru.track.io;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ru.track.io.vendor.Bootstrapper;
import ru.track.io.vendor.FileEncoder;
import ru.track.io.vendor.ReferenceTaskImplementation;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;

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
        final File fout = this.getOutFile(foutPath);
        encodeToBase64(fin, fout);
        return fout;
    }

    @NotNull
    private File getOutFile(@Nullable String foutPath) throws  IOException {
        final File fout;

        if (foutPath != null) {
            fout = new File(foutPath);
        } else {
            fout = File.createTempFile("based_file_", ".txt");
            fout.deleteOnExit();
        }
        return fout;
    }

    private static void encodeToBase64(File fin, File fout) throws IOException{
        int bufferIn, bufferOut;
        int sizeBuffers = 100;
        int inBufferSize = sizeBuffers * 3;
        int outBufferSize = sizeBuffers * 4;
        byte[] bytes;
        bytes = new byte[inBufferSize];
        char[] outputBuffer;
        outputBuffer = new char[outBufferSize];

        try(FileInputStream fis = new FileInputStream(fin);
            FileWriter fous = new FileWriter(fout)) {
            do {
                Arrays.fill(bytes, (byte) 0);
                bufferIn = fis.read(bytes);
                bufferOut = convertAndWriteInBuffer(bytes, bufferIn, outputBuffer);
                fous.write(outputBuffer, 0, bufferOut);
            } while (bufferIn != -1);
        }
    }

    private static int convertAndWriteInBuffer(byte[] bytes, int bufferIn, char[] outputBuffer) {
        int j = 0;
        for (int i=0; i < bufferIn; i+=3, j+=4) {
            int buffer = ((bytes[i] & 0xff) << 16) + ((bytes[i+1] & 0xff) << 8) + ((bytes[i+2] & 0xff));

            outputBuffer[j] = toBase64[((buffer >> 18) & 0x3f)];
            outputBuffer[j+1] = toBase64[((buffer >> 12) & 0x3f)];

            int numEqualsChracters = i + 3 - bufferIn;
            if (numEqualsChracters == 2) {
                outputBuffer[j+2] = '=';
                outputBuffer[j+3] = '=';
            }
            else if (numEqualsChracters == 1) {
                outputBuffer[j+2] = toBase64[((buffer >> 6) & 0x3f)];
                outputBuffer[j+3] = '=';
            }
            else{
                outputBuffer[j+2] = toBase64[((buffer >> 6) & 0x3f)];
                outputBuffer[j+3] = toBase64[(buffer & 0x3f)];
            }
        }
        return j;
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
