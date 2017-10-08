package ru.track.io;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ru.track.io.vendor.Bootstrapper;
import ru.track.io.vendor.FileEncoder;

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

        File fileToRead = new File(finPath);
        File fileToWrite;

        if (foutPath != null) {
            fileToWrite = new File(foutPath);
        } else {
             fileToWrite = File.createTempFile("tmp", ".txt");
             fileToWrite.deleteOnExit();
        }

        InputStream in = new FileInputStream(fileToRead);
        OutputStream out = new FileOutputStream(fileToWrite);
        byte[] bytes = new byte[3];

        int n = 0;
            while ((n = in.read(bytes, 0, 3)) > 0) {
                byte[] b = new byte[n];

                //n - это число байт, которые удалось считать ( их может быть меньше 3)
                for (int i = 0; i < n; i++) {
                    b[i] = bytes[i];
                }

                int dex = 0;
                for(int i = b.length - 1; i >= 0; i--) {
                    //Запись (b[i] & 0xff) возвращает нам int(содержащий 32 бита)
                    //в котором 8 бит это b[i], а остальные нули
                    //эти 8 бит нужно сдвинуть влево в зависимости от номера байта
                    dex |= ((b[b.length - 1 - i] & 0xff) << (8 * i));
                }

                //Получили в dex int, содержащий все наши байты на своих позициях
                for (int i = 0; i < n + 1; i++) {
                    // Находим байт с нужным индексом и наклыдываем на байт маску 111111 = 63
                    // маска соответствует 6 битам
                    // чтобы получить int, соответствующий индексу в массиве символов
                    out.write(toBase64[0x3f & (dex >> 6 * (3 - i))]);
                }
                if (n == 1) {
                    out.write('=');
                    out.write('=');
                }
                if (n == 2) {
                    out.write('=');
                }
            }

        return fileToWrite;
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
