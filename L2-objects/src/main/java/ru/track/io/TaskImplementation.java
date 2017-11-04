package ru.track.io;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ru.track.io.vendor.Bootstrapper;
import ru.track.io.vendor.FileEncoder;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collection;

public final class TaskImplementation implements FileEncoder {

    /**
     * @param finPath  where to read binary data from
     * @param foutPath where to write encoded data. if null, please create and use temporary file.
     * @return file to read encoded data from
     * @throws IOException is case of input/output errors
     */

    @NotNull
    public File encodeFile(@NotNull String finPath, @Nullable String foutPath) throws IOException {
        final int BUFFER_SIZE = 3;
        File fileOut;
        if (foutPath == null) {
            fileOut = File.createTempFile("base64", ".txt");
            fileOut.deleteOnExit();
        }
        else {
            fileOut = new File(foutPath);
        }
        FileWriter writer = new FileWriter(fileOut);
        File file =new File(finPath);
        InputStream in = new FileInputStream(file);
        byte[] buffer = new byte[BUFFER_SIZE];

        int a;
        int b=0;
        int bytesRead = 0;
        int counter=0;
        while ((bytesRead = in.read(buffer)) >= 0){
            for (int i = 0; i < bytesRead; i++){
                if(i==0){
                    a=buffer[i];
                    a=a&0xFF;
                    a>>>=2;
                    writer.write(toBase64[a+b]);
                    b=buffer[i]&3;
                    b<<=4;
                }
                else if(i==1){
                    a=buffer[i];
                    a=a&0xFF;
                    a>>>=4;
                    writer.write(toBase64[a+b]);
                    b=buffer[i]&0x0F;
                    b<<=2;
                }
                else {
                    a=buffer[i];
                    a=a&0xFF;
                    a>>>=6;
                    writer.write(toBase64[a+b]);
                    b=buffer[i]&63;
                    writer.write(toBase64[b]);
                    b=0;
                }
                counter=bytesRead;
            }
        }
        if(counter%3==2){
            writer.write(toBase64[b]);
            writer.write("=");
        }
        else if(counter%3==1){
            writer.write(toBase64[b]);
            writer.write("==");
        }
        writer.flush();
        writer.close();
        in.close();

        return fileOut;
    }


    private static final char[] toBase64 = {
            'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M',
            'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z',
            'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm',
            'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z',
            '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '+', '/'
    };

    private static @NotNull
    File createTempFile() throws IOException {
        final File f = File.createTempFile("test_tempfile_", ".tmp");
        f.deleteOnExit();
        return f;
    }


    public static void main(String[] args) throws IOException {
        final FileEncoder encoder = new TaskImplementation();
        // NOTE: open http://localhost:9000/ in your web browser
        new Bootstrapper(args, encoder).bootstrap(9000);
    }
}
