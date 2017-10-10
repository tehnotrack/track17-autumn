package ru.track.io;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ru.track.io.vendor.Bootstrapper;
import ru.track.io.vendor.FileEncoder;
import ru.track.io.vendor.ReferenceTaskImplementation;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

public final class TaskImplementation implements FileEncoder {

    /**
     * @param finPath  where to read binary data from
     * @param foutPath where to write encoded data. if null, please create and use temporary file.
     * @return file to read encoded data from
     * @throws IOException is case of input/output errors
     */

    void encode1Bytes(byte b1, ArrayList<Character> res) {
        byte temp = 0;
        byte pow = 1;
        for (int j = 4; j <= 128; j *= 2) {
            if ((j & b1) != 0) {
                temp += pow;
            }
            pow *= 2;

        }
        res.add(toBase64[temp]);
        temp = 0;
        pow = 16;
        for (int j = 1; j <= 2; j *= 2) {
            if ((j & b1) != 0) {
                temp += pow;
            }
            pow *= 2;
        }
        res.add(toBase64[temp]);
        res.add('=');
        res.add('=');
    }
    void encode2Bytes(byte b1, byte b2, ArrayList<Character> res) {
        byte temp = 0;
        byte pow = 1;
        for (int j = 4; j <= 128; j *= 2) {
            if ((j & b1) != 0) {
                temp += pow;
            }
            pow *= 2;

        }
        res.add(toBase64[temp]);

        temp = 0;
        pow = 16;
        for (int j = 1; j <= 2; j *= 2) {
            if ((j & b1) != 0) {
                temp += pow;
            }
            pow *= 2;
        }
        pow = 1;
        for (int j = 16; j <= 128; j *= 2) {
            if ((j & b2) != 0) {
                temp += pow;
            }
            pow *= 2;
        }
        res.add(toBase64[temp]);

        temp = 0;
        pow = 4;
        for (int j = 1; j <= 8; j *= 2) {
            if ((j & b2) != 0) {
                temp += pow;
            }
            pow *= 2;
        }
        res.add(toBase64[temp]);
        res.add('=');
    }

    void encode3Bytes(byte b1, byte b2, byte b3, ArrayList<Character> res) {
        byte temp = 0;
        byte pow = 1;
        for (int j = 4; j <= 128; j *= 2) {
            if ((j & b1) != 0) {
                temp += pow;
            }
            pow *= 2;

        }
        res.add(toBase64[temp]);

        temp = 0;
        pow = 16;
        for (int j = 1; j <= 2; j *= 2) {
            if ((j & b1) != 0) {
                temp += pow;
            }
            pow *= 2;
        }
        pow = 1;
        for (int j = 16; j <= 128; j *= 2) {
            if ((j & b2) != 0) {
                temp += pow;
            }
            pow *= 2;
        }
        res.add(toBase64[temp]);

        temp = 0;
        pow = 4;
        for (int j = 1; j <= 8; j *= 2) {
            if ((j & b2) != 0) {
                temp += pow;
            }
            pow *= 2;
        }
        pow = 1;
        for (int j = 64; j <= 128; j *=2) {
            if ((j & b3) != 0) {
                temp += pow;
            }
            pow *= 2;
        }
        res.add(toBase64[temp]);

        temp = 0;
        pow = 1;
        for (int j = 1; j <= 32; j *=2) {
            if ((j & b3) != 0) {
                temp += pow;
            }
            pow *= 2;
        }
        res.add(toBase64[temp]);
    }

    @NotNull
    public File encodeFile(@NotNull String finPath, @Nullable String foutPath) throws IOException {
        Path path;
        try {
            path = Paths.get(finPath);
        }
        catch (InvalidPathException name) {
            throw new IOException("Having exception " + name);
        }

        byte[] bytes = Files.readAllBytes(path);

        ArrayList<Character> res = new ArrayList<>();
        for (int i = 0; i < bytes.length - 2; i += 3) {
            encode3Bytes(bytes[i], bytes[i + 1], bytes[i + 2], res);
        }
        if (bytes.length % 3 == 1) {
            encode1Bytes(bytes[bytes.length - 1], res);
        } else if (bytes.length % 3 == 2) {
            encode2Bytes(bytes[bytes.length - 2], bytes[bytes.length - 1], res);
        }
        if (foutPath == null) {
            foutPath = "./res.txt";
        }
        FileOutputStream outputstream = new FileOutputStream(foutPath);
        for (int i = 0; i < res.size(); i++) {
            outputstream.write(res.get(i));
        }
        outputstream.close();
        File file = new File(foutPath);
        return file;
    }

    private static final char[] toBase64 = {
            'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M',
            'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z',
            'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm',
            'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z',
            '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '+', '/'
    };

    public static void main(String[] args) throws IOException {
        System.out.println(args.length);
        System.out.println(args[0]);
        final FileEncoder encoder = new TaskImplementation();
        // NOTE: open http://localhost:9000/ in your web browser
        new Bootstrapper(args, encoder).bootstrap(9000);
    }

}
