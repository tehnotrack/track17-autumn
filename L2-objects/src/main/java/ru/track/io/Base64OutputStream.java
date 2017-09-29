package ru.track.io;

import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class Base64OutputStream extends FilterOutputStream {

    Base64OutputStream(OutputStream os) {
        super(os);
    }

    @Override
    public void write(byte[] b, int off, int len) throws IOException {
        int lengthOfChunk = 3;
        int lengthWithoutLastChunk = len / lengthOfChunk * lengthOfChunk;
        if (lengthWithoutLastChunk != 0) {
            for (int i = 0; i < lengthWithoutLastChunk; i += 3) {
                int part = (b[i] & 0xff) << 16 |
                        (b[i + 1] & 0xff) << 8 |
                        (b[i + 2] & 0xff);
                out.write(toBase64[part >>> 18 & 0x3f]);
                out.write(toBase64[part >>> 12 & 0x3f]);
                out.write(toBase64[part >>> 6 & 0x3f]);
                out.write(toBase64[part & 0x3f]);
            }
        }
        if ((len - lengthWithoutLastChunk) == 2) {
            int part = (b[lengthWithoutLastChunk] & 0xff) << 16 |
                    (b[lengthWithoutLastChunk + 1] & 0xff) << 8;
            out.write(toBase64[part >>> 18 & 0x3f]);
            out.write(toBase64[part >>> 12 & 0x3f]);
            out.write(toBase64[part >>> 6 & 0x3c]);
            out.write('=');
        } else if ((len - lengthWithoutLastChunk) == 1) {
            int part = (b[lengthWithoutLastChunk] & 0xff) << 16;
            out.write(toBase64[part >>> 18 & 0x3f]);
            out.write(toBase64[part >>> 12 & 0x30]);
            out.write('=');
            out.write('=');
        }
    }
    private static final char[] toBase64 = {
            'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M',
            'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z',
            'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm',
            'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z',
            '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '+', '/'
    };
}