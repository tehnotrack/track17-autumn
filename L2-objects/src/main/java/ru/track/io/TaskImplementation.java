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
            fout = File.createTempFile("Base64", ".txt");
            fout.deleteOnExit();
        }
        else fout = new File(foutPath);


        InputStream is = new FileInputStream(fin);


        long length = fin.length();


        byte[] bytes = new byte[(int)length];


        int offset = 0;

        int numRead = 0;

        while (offset < bytes.length && (numRead=is.read(bytes, offset, bytes.length-offset)) >= 0)
        {
            offset += numRead;
        }

        if (offset < bytes.length)
        {
            throw new IOException("Could not completely read file "+fin.getName());
        }


        String toBase64 = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/";
        String encoded = "";

        int paddingCount = (3 - (bytes.length % 3)) % 3;

        byte[] padded = new byte[bytes.length + paddingCount];
        System.arraycopy(bytes, 0, padded, 0, bytes.length);
        bytes = padded;

        for (int i = 0; i < bytes.length; i += 3) {
            int j = ((bytes[i] & 0xff) << 16) + ((bytes[i + 1] & 0xff) << 8) + (bytes[i + 2] & 0xff);
            encoded = encoded + toBase64.charAt((j >> 18) & 0x3f) + toBase64.charAt((j >> 12) & 0x3f) + toBase64.charAt((j >> 6) & 0x3f) + toBase64.charAt(j & 0x3f);
        }

        encoded = encoded.substring(0, encoded.length() - paddingCount) + "==".substring(0, paddingCount);



        PrintWriter pw = new PrintWriter( fout );
               pw.write(encoded);
               pw.close();

        return fout;

        }


    public static void main(String[] args) throws IOException {
        final FileEncoder encoder = new TaskImplementation();
        // NOTE: open http://localhost:9000/ in your web browser
        new Bootstrapper(args, encoder).bootstrap(9000);
    }

}
