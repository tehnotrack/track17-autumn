package ru.track.io;

import org.apache.commons.io.IOUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ru.track.io.vendor.Bootstrapper;
import ru.track.io.vendor.FileEncoder;
import ru.track.io.vendor.ReferenceTaskImplementation;

import java.io.*;

public final class TaskImplementation implements FileEncoder {


    private int toUnsigned(byte[] buffer)
    {
        int unsigned =0;
        for(int i=0;i<3;++i)
        {
            int mid;
            if(buffer[i]<0) mid=(buffer[i]&127)+128;
            else mid=buffer[i];
            mid=mid<<((2-i)*8);
            unsigned+=mid;
        }
        return unsigned;
    }

    @NotNull
    public File encodeFile(@NotNull String finPath, @Nullable String foutPath) throws IOException
    {
        final File fin = new File(finPath);
        final File fout;

        if (foutPath != null)
        {
            fout = new File(foutPath);
        }
        else
        {
            fout = File.createTempFile("based_file_", ".txt");
            fout.deleteOnExit();
        }
        FileInputStream fis = new FileInputStream(fin);
        OutputStream os = new FileOutputStream(fout);

        int s;
        byte[] buffer=new byte[3];
        s=fis.read(buffer);
        int unsigned = toUnsigned(buffer);
        int index;
        while (s>=0)
        {
            for(int i=0;i<4;++i)
            {
                index=((unsigned<<(i*6+8))>>(18+8))&63;
                os.write(toBase64[index]);
            }
            for(int i=0;i<3;++i) buffer[i]=0;
            s=fis.read(buffer);
            unsigned=toUnsigned(buffer);
        }
        return fout;
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
