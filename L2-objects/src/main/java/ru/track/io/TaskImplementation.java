package ru.track.io;

import org.apache.commons.io.IOUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ru.track.io.vendor.Bootstrapper;
import ru.track.io.vendor.FileEncoder;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public final class TaskImplementation implements FileEncoder {


    /**
     * @param finPath  where to read binary data from
     * @param foutPath where to write encoded data. if null, please create and use temporary file.
     * @return file to read encoded data from
     * @throws IOException is case of input/output errors
     */

    public File encodeFile(@NotNull String finPath, @Nullable String foutPath) throws IOException {

        final File fin = new File(finPath);
        final File fout;
        if (foutPath != null) {
            fout = new File(foutPath);
        } else {
            fout = File.createTempFile("based_file_", ".txt");
            fout.deleteOnExit();
        }
        try (
                final FileInputStream fis = new FileInputStream(fin);
                final FileOutputStream fos = new FileOutputStream(fout);
                final BufferedInputStream bis =  new BufferedInputStream(fis);
                final BufferedOutputStream bos = new BufferedOutputStream(fos);
        ) {
            int bytesCopied = IOUtils.copy(fis, bos); // result unused
            bos.flush();
        }

        return fout;


        /* XXX: https://docs.oracle.com/javase/8/docs/api/java/io/File.html#deleteOnExit-- */
//заменить на bufferedinputstream
//        Path path = Paths.get(finPath);

/*        byte[] content = Files.readAllBytes(path);
        String encodedStr = encode(content);

        byte [] writeValue = encodedStr.getBytes();
        if (foutPath == null)
            foutPath = "./result_pic";
        FileOutputStream outputstream = new FileOutputStream(foutPath);
        outputstream.write(writeValue);
        outputstream.close();
        File retValue = new File(foutPath);

        return retValue;
*/
    }

    private static final char[] toBase64 = {
            'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M',
            'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z',
            'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm',
            'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z',
            '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '+', '/'
    };


    private String encode(byte[] data)
    {

        StringBuilder buffer = new StringBuilder();
        int align = 0;
        for (int i = 0; i < data.length; i += 3) {

            int data3bytes = 0;
            for (int j = i; (j - i) < 3; ++j) {
                if (j >= data.length) {
                    ++align;
                    continue;
                }
                data3bytes |= ((data[j] & 0xFF) << (8*(2 - (j - i))));
            }

            for (int j = 0; j < 4 - align; j++) { // дозаписать
                int c = ((data3bytes <<  6*j) & 0xFC0000) >> 18; // FC - старшие 6 единиц
                buffer.append(toBase64[c]);
            }
        }
        for (int j = 0; j < align; j++) {
            buffer.append("="); // последнии символы, в случае, если ровно по 3 разбить нельзя заполняются "="
        }

        return buffer.toString();
    }


    public static void main(String[] args) throws IOException {
        final FileEncoder encoder = new TaskImplementation();
        // NOTE: open http://localhost:9000/ in your web browser
        new Bootstrapper(args, encoder).bootstrap(9000);
    }

}
