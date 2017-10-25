package ru.track.io;

import org.apache.commons.io.FileUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ru.track.io.vendor.Bootstrapper;
import ru.track.io.vendor.FileEncoder;
import ru.track.io.vendor.ReferenceTaskImplementation;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.awt.image.WritableRaster;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
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
        /* XXX: https://docs.oracle.com/javase/8/docs/api/java/io/File.html#deleteOnExit-- */
        Path fileLocation = Paths.get(finPath);
        File fileOut;
        byte[] imageBytes = Files.readAllBytes(fileLocation);;
        if (foutPath == null) {
            fileOut = File.createTempFile("base64", ".txt");
            fileOut.deleteOnExit();
        }
        else {
            fileOut = new File(foutPath);
        }
        FileWriter writer = new FileWriter(fileOut);
        int a;
        int b=0;
        int count =0;
        for (int i = 0; i < imageBytes.length; ++i) {
            if(count%3==0){
                a=imageBytes[i];
                a=a&0xFF;
                a>>>=2;
                writer.write(toBase64[a+b]);
                System.out.print(toBase64[a+b]);
                b=imageBytes[i]&3;
                b<<=4;
                count++;
            }else if (count%3==1){
                a=imageBytes[i];
                a=a&0xFF;
                a>>>=4;
                try {
                    writer.write(toBase64[a+b]);
                    //System.out.print(toBase64[a+b]);
                }catch (Exception e){
                    System.out.println(e);
                }
                b=imageBytes[i]&0x0F;
                b<<=2;
                count++;
            }
            else{
                a=imageBytes[i];
                a=a&0xFF;
                a>>>=6;
                try {
                    writer.write(toBase64[a+b]);
                    //System.out.print(toBase64[a+b]);
                }catch (Exception e){
                    System.out.println(e);
                }
                b=imageBytes[i]&63;
                try {
                    writer.write(toBase64[b]);
                    //System.out.print(toBase64[b]);
                }catch (Exception e){
                    System.out.println(e);
                }
                b=0;
                count++;
            }
        }
        if(imageBytes.length%3!=0){
            writer.write(toBase64[b]);
            System.out.print(toBase64[b]);
        }
        if(imageBytes.length%3==2){
            writer.write("=");
        }
        else if(imageBytes.length%3==1){
            writer.write("==");
        }
        //System.out.println(result);
        writer.flush();

        return fileOut;
    }


    private static final char[] toBase64 = {
            'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M',
            'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z',
            'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm',
            'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z',
            '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '+', '/'
    };

    public static Collection<String> data() {
        return Arrays.asList(
                "xxx",
                "xx",
                "x",
                "xxx000",
                "xxx00",
                "xxx0"
        );
    }

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
