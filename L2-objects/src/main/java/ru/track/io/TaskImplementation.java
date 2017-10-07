package ru.track.io;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ru.track.io.vendor.Bootstrapper;
import ru.track.io.vendor.FileEncoder;
import ru.track.io.vendor.ReferenceTaskImplementation;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;

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
        FileInputStream input = new FileInputStream(finPath);
        File outFile;
        if (foutPath != null) {
            outFile = new File(foutPath);
        } else {
            outFile = File.createTempFile("temporary_file", ".tmp");
            outFile.deleteOnExit();
        }
        FileWriter output = new FileWriter(outFile);
        while (input.available() > 0) {
            byte bytes[] = new byte[3];
            char characters[] = new char[4];
            int bytesRead = input.read(bytes);
            // & 0xff is applied to get 8 lower bits (for correct cast to unsigned int)
            characters[0] = toBase64[(bytes[0] & 0xff) >> 2];
            // & 0x3 is applied to get 2 lower bits
            characters[1] = toBase64[((bytes[0] & 0x3) << 4) + ((bytes[1] & 0xff) >> 4)];
            if (bytesRead >= 2) {
                // & 0xf is applied to get 4 lower bits
                characters[2] = toBase64[((bytes[1] & 0xf) << 2) + ((bytes[2] & 0xff) >> 6)];
            } else {
                characters[2] = '=';
            }
            if (bytesRead >= 3) {
                // & 0x3f is applied to get 6 lower bits
                characters[3] = toBase64[(bytes[2] & 0x3f)];
            } else {
                characters[3] = '=';
            }
            output.write(characters);
        }
        output.close();
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
