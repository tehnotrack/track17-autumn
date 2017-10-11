package ru.track.io;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ru.track.io.vendor.Bootstrapper;
import ru.track.io.vendor.FileEncoder;
import ru.track.io.vendor.ReferenceTaskImplementation;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;

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
        File fileIn = new File(finPath);
        File fileOut;
        //throw new UnsupportedOperationException(); // TODO: implement
        BufferedImage bufferedImage = ImageIO.read(fileIn);
        WritableRaster raster = bufferedImage .getRaster();
        DataBufferByte data   = (DataBufferByte) raster.getDataBuffer();
        StringBuilder buffer = new StringBuilder();
        byte[] imageBytes = data.getData();
        int pad = 0;
        String bits="";
        String result="";
        int a;
        int b=0;
        int zdvig1=2;
        int zdvig2=4;
        int count =0;
        for (int i = 0; i < imageBytes.length; i += 3) {
            if(count%3==0){
                a=imageBytes[i];
                a>>>=2;
                result+=toBase64[a+b];
                b=imageBytes[i]&2;
                b<<=4;
                count++;
            }else if (count%3==1){
                a=imageBytes[i];
                a>>>=4;
                result+=toBase64[a+b];
                b=imageBytes[i]&0x0F;
                b<<=2;
                count++;
            }
            else{
                a=imageBytes[i];
                a>>>=6;
                result+=toBase64[a+b];
                b=imageBytes[i]&63;
                result+=toBase64[b];
                b=0;
                count++;
            }
        }
        if(foutPath == null) {
            fileOut = new File("C:\\Users\\mariia\\track17-autumn\\L2-objects\\result.txt");
            try(FileWriter writer = new FileWriter("C:\\SomeDir\\notes3.txt", false))
            {
                // запись всей строки
                writer.write(result);
                // запись по символам
                writer.flush();
            }
            catch(IOException ex){

                System.out.println(ex.getMessage());
            }
        } else {
            fileOut = new File(foutPath);
            try(FileWriter writer = new FileWriter(foutPath, false))
            {
                // запись всей строки
                writer.write(result);
                // запись по символам
                writer.flush();
            }
            catch(IOException ex){

                System.out.println(ex.getMessage());
            }
        }

        return fileOut;
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
