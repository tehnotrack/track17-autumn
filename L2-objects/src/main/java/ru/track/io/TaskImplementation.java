package ru.track.io;

import org.apache.commons.io.IOUtils;
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
        final File fin = new File(finPath);
        final File fout;

        if (foutPath != null) {
            fout = new File(foutPath);
        } else {
            fout = new File("temp_file.txt");
            fout.createNewFile();
            fout.deleteOnExit();
        }
        byte[] in = new byte[(int)fin.length()];
        try(BufferedInputStream reader = new BufferedInputStream (new FileInputStream(fin)))
        {
            int c;
            int i = 0;
            while((c=reader.read()) != -1){
                in[i++] = (byte)c;
            }
            reader.close();
        }

        try(BufferedOutputStream writer = new BufferedOutputStream(new FileOutputStream(fout)))
        {
            writer.write(Base64(in));
            writer.flush();
            writer.close();
        }
        return fout;
    }

    private static byte[] Base64(byte[] in){
        int len = in.length;
        if (len%3 == 1) {
            len += 2;
        } else if (len%3 == 2) {
            len += 1;
        }
        byte[] res = new byte[len * 4 / 3];
        int j = 0;
        for(int i = 2; i < in.length; i+=3){
            byte byte1 = in[i - 2];
            byte byte2 = in[i - 1];
            byte byte3 = in[i];

            byte newByte1 = (byte)((byte1 >> 2) & 0b111111);
            byte newByte2 = (byte)((byte1 << 4) & 0b110000 | (byte2 >> 4) & 0b001111);
            byte newByte3 = (byte)((byte2 << 2) & 0b111100 | (byte3 >> 6) & 0b000011);
            byte newByte4 = (byte)(byte3 & 0b111111);

            res[j++] = (byte) toBase64[newByte1];
            res[j++] = (byte) toBase64[newByte2];
            res[j++] = (byte) toBase64[newByte3];
            res[j++] = (byte) toBase64[newByte4];
        }

        if(in.length % 3 == 1){
            byte byte1 = in[in.length - 1];

            byte newByte1 = (byte)((byte1 >> 2) & 0b111111);
            byte newByte2 = (byte)((byte1 << 4) & 0b110000);

            res[j++] = (byte) toBase64[newByte1];
            res[j++] = (byte) toBase64[newByte2];
            res[j++] = (byte)('=');
            res[j++] = (byte)('=');
        } else if(in.length % 3 == 2){
            byte byte1 = in[in.length - 1];
            byte byte2 = in[in.length - 1];

            byte newByte1 = (byte)((byte1 >> 2) & 0b111111);
            byte newByte2 = (byte)((byte1 << 4) & 0b110000 | (byte2 >> 4) & 0b001111);
            byte newByte3 = (byte)((byte2 << 2) & 0b111100);

            res[j++] = (byte) toBase64[newByte1];
            res[j++] = (byte) toBase64[newByte2];
            res[j++] = (byte) toBase64[newByte3];
            res[j++] = (byte)('=');
        }
        return res;
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
