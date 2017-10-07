package ru.track.io;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ru.track.io.vendor.Bootstrapper;
import ru.track.io.vendor.FileEncoder;
import ru.track.io.vendor.ReferenceTaskImplementation;

import java.io.*;
import java.util.Arrays;

public final class TaskImplementation implements FileEncoder {

    private int toUnsignedByte(byte value) {
        return value & 0xFF;
    }

    private void encode(FileInputStream inputStream, BufferedWriter outputWriter) throws IOException {
        byte[] buffer = new byte[3];

        int nRead;
        while((nRead = inputStream.read(buffer)) != -1) {
            int fullBitMask = toUnsignedByte(buffer[0]);

            StringBuilder reversedResultBuilder = new StringBuilder();

            for (int i = 1; i < 3; i++) {
                fullBitMask = fullBitMask * 256 + toUnsignedByte(buffer[i]);
            }

            for (int i = 0; i < 4; i++) {
                if (nRead < 3 && i == 0 || nRead == 1 && i == 1) {
                    reversedResultBuilder.append("=");
                } else {
                    reversedResultBuilder.append(toBase64[fullBitMask % 64]);
                }

                fullBitMask >>>= 6;
            }

            outputWriter.write(reversedResultBuilder.reverse().toString());
            Arrays.fill(buffer, (byte)0);
        }
    }

    /**
     * @param finPath  where to read binary data from
     * @param foutPath where to write encoded data. if null, please create and use temporary file.
     * @return file to read encoded data from
     * @throws IOException is case of input/output errors
     */
    @NotNull
    public File encodeFile(@NotNull String finPath, @Nullable String foutPath) throws IOException {
        final File inputFile = new File(finPath);
        final File outputFile;

        if (foutPath == null) {
            outputFile = File.createTempFile("tem", ".txt");
            outputFile.deleteOnExit();
        } else {
            outputFile = new File(foutPath);
        }

        try (FileInputStream inputStream = new FileInputStream(inputFile);
             BufferedWriter outputWriter = new BufferedWriter(new FileWriter(outputFile))) {

            encode(inputStream, outputWriter);
        }

        return outputFile;
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
