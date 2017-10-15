package ru.track.io;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ru.track.io.vendor.Bootstrapper;
import ru.track.io.vendor.FileEncoder;
import ru.track.io.vendor.ReferenceTaskImplementation;

import java.io.*;
import java.util.Arrays;

public final class TaskImplementation implements FileEncoder {

    private void encode(FileInputStream inputStream, BufferedWriter outputWriter) throws IOException {
        byte[] buffer = new byte[3];

        int nRead;
        while((nRead = inputStream.read(buffer)) != -1) {
            int bitwiseRepresentation = 0;

            for (int i = 0; i < 3; i++) {
                bitwiseRepresentation <<= 8;
                bitwiseRepresentation += buffer[i] & 0xFF;
            }

            char[] resultSymbols = new char[4];

            for (int i = 0; i < 4; i++) {
                if (nRead < 3 && i == 0 || nRead == 1 && i == 1) {
                    resultSymbols[3 - i] = '=';
                } else {
                    resultSymbols[3 - i] = toBase64[bitwiseRepresentation % 64];
                }

                bitwiseRepresentation >>>= 6;
            }

            for (int i = 0; i < 4; i++) {
                outputWriter.write(resultSymbols[i]);
            }

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
