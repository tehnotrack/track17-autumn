package ru.track.io;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ru.track.io.vendor.Bootstrapper;
import ru.track.io.vendor.FileEncoder;
import ru.track.io.vendor.ReferenceTaskImplementation;
import java.io.FileInputStream;
import java.io.FileOutputStream;
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
        int bufferIn;
        int bufferSize = 3;
        byte[] bytes;
        bytes = new byte[bufferSize];

        try(FileInputStream fis = new FileInputStream(fin);
            FileOutputStream fous = new FileOutputStream(fout)) {
            do {
                Arrays.fill(bytes, (byte) 0);
                bufferIn = fis.read(bytes);
                int buffer = ((bytes[0] & 0xff) << 16) + ((bytes[1] & 0xff) << 8) + ((bytes[2] & 0xff));
                writeBufferData(buffer, bufferIn, fous);
            } while (bufferIn != -1);
        }
    }

    private static void writeBufferData(int buffer, int bufferIn, FileOutputStream fous) throws IOException{
        if (bufferIn == 3) {
            convertAndWrite(buffer, 18, fous);
            convertAndWrite(buffer, 12, fous);
            convertAndWrite(buffer, 6, fous);
            convertAndWrite(buffer, 0, fous);
        } else if (bufferIn == 2) {
            convertAndWrite(buffer, 18, fous);
            convertAndWrite(buffer, 12, fous);
            convertAndWrite(buffer, 6, fous);
            fous.write('=');
        } else if (bufferIn == 1) {
            convertAndWrite(buffer, 18, fous);
            convertAndWrite(buffer, 12, fous);
            fous.write('=');
            fous.write('=');
        }
    }

    private static void convertAndWrite(int buffer, int shift, FileOutputStream fous) throws IOException{
        fous.write(toBase64[((buffer >> shift) & 0x3f)]);
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
