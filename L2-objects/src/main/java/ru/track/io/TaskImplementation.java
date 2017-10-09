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
     * @throws IOException in case of input/output errors
     */
    @NotNull
    public File encodeFile(@NotNull String finPath, @Nullable String foutPath) throws IOException {
        /* XXX: https://docs.oracle.com/javase/8/docs/api/java/io/File.html#deleteOnExit-- */
        File outFile;
        if (foutPath != null) {
            outFile = new File(foutPath);
        } else {
            outFile = File.createTempFile("temporary_file", ".tmp");
            outFile.deleteOnExit();
        }
        try (final BufferedInputStream input = new BufferedInputStream(new FileInputStream(finPath));
                final BufferedWriter output = new BufferedWriter(new FileWriter(outFile))) {
            while (input.available() > 0) {
                int firstByte = input.read();
                int secondByte = input.read();
                int thirdByte = input.read();
                // & 0xff is applied to get 8 lower bits (for correct cast to unsigned int)
                output.write(toBase64[(firstByte & 0xff) >> 2]);
                if (secondByte == -1) {
                    // & 0x3 is applied to get 2 lower bits
                    output.write(toBase64[((firstByte & 0x3) << 4)]);
                    output.write('=');
                    output.write('=');
                    continue;
                }
                output.write(toBase64[((firstByte & 0x3) << 4) + ((secondByte & 0xff) >> 4)]);
                if (thirdByte == -1) {
                    // & 0xf is applied to get 4 lower bits
                    output.write(toBase64[((secondByte & 0xf) << 2)]);
                    output.write('=');
                    continue;
                }
                output.write(toBase64[((secondByte & 0xf) << 2) + ((thirdByte & 0xff) >> 6)]);
                // & 0x3f is applied to get 6 lower bits
                output.write(toBase64[(thirdByte & 0x3f)]);
                continue;
            }
        }
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
        final FileEncoder encoder = new TaskImplementation();
        // NOTE: open http://localhost:9000/ in your web browser
        new Bootstrapper(args, encoder).bootstrap(9000);
    }

}
