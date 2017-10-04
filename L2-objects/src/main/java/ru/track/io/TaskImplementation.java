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
        /* XXX: https://docs.oracle.com/javase/8/docs/api/java/io/File.html#deleteOnExit-- */
        /*throw new UnsupportedOperationException();*/ // TODO: implement

        final File fin = new File(finPath);
        final File fout;

        if (foutPath != null) {
            fout = new File(foutPath);
        } else {
            fout = File.createTempFile("Base64EncodedFile", ".txt");
            fout.deleteOnExit();
        }

        final FileInputStream inputStream = new FileInputStream(fin);
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        StringBuilder stringBuilderEncodedBase64 = new StringBuilder();
        String resultStrring;
        int pads = 0;

        while (true) {
            int oneByte = inputStream.read();
            if (oneByte != -1) {
                int twoByte = inputStream.read();
                int threeByte = inputStream.read();

                int pad = ((oneByte & 0xFF) << 16) | ((Math.max(twoByte, 0) & 0xFF) << 8) | (Math.max(threeByte, 0) & 0xFF);

                stringBuilderEncodedBase64.append(toBase64[pad >> 18 & 63]);
                stringBuilderEncodedBase64.append(toBase64[pad >> 12 & 63]);
                stringBuilderEncodedBase64.append(twoByte == -1 ? '=' : toBase64[pad >> 6 & 63]);
                stringBuilderEncodedBase64.append(threeByte == -1 ? '=' : toBase64[pad & 63]);
            } else {
                break;
            }
        }

        resultStrring = stringBuilderEncodedBase64.toString();
        PrintWriter printResultBase64Encoded = new PrintWriter(fout);
        printResultBase64Encoded.write(resultStrring);
        printResultBase64Encoded.close();

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
