package ru.track.io;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ru.track.io.vendor.Bootstrapper;
import ru.track.io.vendor.FileEncoder;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;




public final class TaskImplementation implements FileEncoder {

    private StringBuilder sb = new StringBuilder();

    private void encodeTriplet(byte b1, byte b2, byte b3) {
        int x = (int) b3 | ((int) b2 << 8) | ((int) b1 << 16);
        int cb4 = x << 26 >>> 26;
        int cb3 = x << 20 >>> 26;
        int cb2 = x << 14 >>> 26;
        int cb1 = x << 8 >>> 26;
        sb.append(toBase64[cb1]).append(toBase64[cb2]).append(toBase64[cb3]).append(toBase64[cb4]);
    }

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
            fout = File.createTempFile("Base64", ".tmp");
            fout.deleteOnExit();
        }
        else fout = new File(foutPath);

        byte[] fileArray = Files.readAllBytes(fin.toPath());
        int len = fileArray.length;
        sb.ensureCapacity(len);
        int remainder = len % 3;
        for (int i = 0; i < len - remainder; i += 3) {
            encodeTriplet(fileArray[i], fileArray[i+1], fileArray[i+2]);
        }
        if (remainder == 2) {
            encodeTriplet(fileArray[len - 2], fileArray[len - 1], (byte) 0);
            sb.setCharAt(sb.length() - 1, '=');
        } else if (remainder == 1) {
            encodeTriplet(fileArray[len - 1], (byte) 0, (byte) 0);
            sb.replace(sb.length() - 2, sb.length(), "==");
        }

        PrintWriter pw = new PrintWriter( fout );
        pw.write(sb.toString());
        pw.close();

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
        // NOTE: open http://localhost:9000/ in your web browser
        new Bootstrapper(args, encoder).bootstrap(9000);
    }

}
