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

            final File fin = new File(finPath);
            final File fout;

            if (foutPath != null) {
                fout = new File(foutPath);
            } else {
                fout = File.createTempFile("based_file_", ".txt");
                fout.deleteOnExit();
            }

            FileReader fr = new FileReader(fin);
            BufferedReader bf = new BufferedReader(fr); // read


            PrintWriter fout1 = new PrintWriter(fout.getAbsoluteFile());

            StringBuilder sb = new StringBuilder();

            Integer c;

            int [] arr = new int[4];

            for (int i = 1; ; i++) {
                if (i%4 != 0) {
                    c = bf.read();
                    if (c == -1)
                    {
                        if (i % 4 == 2) {
                            sb.append ("0000");
                            for (int j = 0; j < 2; j++) {
                                arr[j] = Integer.parseInt(sb.substring(6 * j, 6 * (j + 1)), 2);
                                fout1.print(toBase64[arr[j]]);
                            }
                            fout1.print('=');fout1.print('=');
                        }
                        if (i % 4 == 3) {
                            sb.append ("00");
                            for (int j = 0; j < 3; j++) {
                                arr[j] = Integer.parseInt(sb.substring(6 * j, 6 * (j + 1)), 2);
                                fout1.print(toBase64[arr[j]]);
                            }
                            fout1.print('=');
                        }
                        break;
                    }
                    else {
                        for (int k = 0; k < 8 - Integer.toBinaryString(c).length(); k++)
                            sb.insert (8 * (i % 4 - 1) + k, "0");
                        sb.append(Integer.toBinaryString(c));
                    }

                }
                else {
                    for (int j = 0; j < 4; j++) {
                        arr[j] = Integer.parseInt(sb.substring(6 * j, 6 * (j + 1)), 2);
                        fout1.print(toBase64[arr[j]]);
                    }
                    sb.setLength(0);
                }
            }

            fout1.close();
            return fout;

        /* XXX: https://docs.oracle.com/javase/8/docs/api/java/io/File.html#deleteOnExit-- */
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
        new Bootstrapper(args, encoder).bootstrap(8000);
    }

}
