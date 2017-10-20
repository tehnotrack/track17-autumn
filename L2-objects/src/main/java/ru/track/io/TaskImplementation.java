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
        File fin = new File(finPath);
        File fout;
        if (foutPath == null) {
            fout = File.createTempFile("my_temp_output", ".txt");
            fout.deleteOnExit();
        } else {
            fout = new File(foutPath);
        }
        try (BufferedInputStream reader = new BufferedInputStream(new FileInputStream(fin));
             BufferedWriter writer = new BufferedWriter(new FileWriter(fout))) {
            int sixMask = 0x3F, threeBytes, secondByte, thirdByte;
            while ((threeBytes = reader.read()) != -1) {
                secondByte = reader.read();
                thirdByte = reader.read();
                int bound = 0;
                if (secondByte == -1) {
                    secondByte = 0;
                    thirdByte = 0;
                    bound = 2;
                } else if (thirdByte == -1) {
                    thirdByte = 0;
                    bound = 1;
                }
                threeBytes = (threeBytes << 16) | (secondByte << 8) | (thirdByte);
                for (int i = 3; i >= bound; i--) writer.write(toBase64[(threeBytes >> (i * 6)) & sixMask]);
                for (int i = 0; i < bound; i++) writer.write('=');
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
        new Bootstrapper(args, encoder).bootstrap(9000);
    }

}
