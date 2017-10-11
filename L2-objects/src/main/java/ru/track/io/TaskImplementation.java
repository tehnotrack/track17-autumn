package ru.track.io;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ru.track.io.vendor.Bootstrapper;
import ru.track.io.vendor.FileEncoder;
import ru.track.io.vendor.ReferenceTaskImplementation;

import java.io.*;
import java.math.*;



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

            BufferedInputStream bis = new BufferedInputStream(new FileInputStream(fin));
            BufferedOutputStream fout1 = new BufferedOutputStream(new FileOutputStream(fout)); //стрим для записи в файл

            int num_of_bytes_read, num;

            byte [] arr = new byte[3];

            while ((num_of_bytes_read = bis.read(arr, 0, 3)) != -1) {
                if (num_of_bytes_read == 3) {
                    num = ((arr[0] & 0xff) << 16) + ((arr[1] & 0xff) << 8) + (arr[2] & 0xff);

                    for (int i = 0; i < 4; i++)
                        fout1.write (toBase64[(num >> 18 - 6*i) & 0b111111]);
                }
                else {
                    for (int i = num_of_bytes_read; i < 3; i++)
                        arr[i] = 0;

                    num = ((arr[0] & 0xff) << 16) + ((arr[1] & 0xff) << 8) + (arr[2] & 0xff);

                    for (int i = 0; i < 4; i++) {
                        if (i < num_of_bytes_read + 1) fout1.write(toBase64[(num >> 18 - 6*i) & 0b111111]);
                        else fout1.write('=');
                    }
                }
            }
            fout1.close();
            return fout;

            //-----------------------------------------------------------------------------------------------------
            // -------ниже старая версия кода, рабочая, но там все муторно и построено все на работе со строками
            //-----------------------------------------------------------------------------------------------------


            /*

            StringBuilder sb = new StringBuilder();//в sb лежит строка в 24 элемента (24 бита соответственно)

            Integer c;                  //считываю символ сюда

            int [] arr = new int[4];                //массив, в который записываю 4 числа по 6 бит после их обработки

            for (int i = 1; ; i++) {                //цикл в 4 хода: если i%4 отлично от нуля, то есть 1,2,3,
                if (i%4 != 0) {                     //то я дописываю символ который считал в двоичном виде в sb
                    c = bis.read();                 //если же i%4==0 то идет обработка того, что у меня лежит в sb
                    if (c == -1)
                    {
                        //тут обработка исключительных случаев: когда файл закончился,
                        //но мы успели считать только 1 или 2 байта (соответственно это когда
                        //i%4==2 или 3

                        if (i % 4 == 2) {
                            sb.append ("0000");          //если считали 1 байт, то надо в sb дописать 4 нуля, чтобы добить
                                                         //до 12 бит
                            for (int j = 0; j < 2; j++) {           //обработка этих 12 бит и запись в выходной файл
                                arr[j] = Integer.parseInt(sb.substring(6 * j, 6 * (j + 1)), 2);
                                fout1.print(toBase64[arr[j]]);
                            }
                            fout1.print('=');fout1.print('='); //в конце 2 '='
                        }
                        if (i % 4 == 3) {               //то же самое только когда успели считать 2 байта
                            sb.append ("00");
                            for (int j = 0; j < 3; j++) {
                                arr[j] = Integer.parseInt(sb.substring(6 * j, 6 * (j + 1)), 2);
                                fout1.print(toBase64[arr[j]]);
                            }
                            fout1.print('=');
                        }
                        break;
                    }

                    else {      //случай, когда надо считывать, но и конца файла мы еще не достигли:
                        for (int k = 0; k < 8 - Integer.toBinaryString(c).length(); k++)
                            sb.insert (8 * (i % 4 - 1) + k, "0"); //по выхлопам, понял что компилятор опускает
                                                                             //нули которые идут в начале. то есть чтобы мой алгоритм
                                                                             //работал правильно, надо их туда добавить
                        sb.append(Integer.toBinaryString(c));                //тут уже после того как добавил нужное число нулей, пишу само число в 2чном виде
                    }

                }

                else {   //случай когда надо обработать то, что записал в sb (если зашел сюда, значит гарантировано в
                         //sb лежит 24 бита
                    for (int j = 0; j < 4; j++) {
                        //arr[j] = Integer.parseInt(sb.substring(6 * j, 6 * (j + 1)), 2); //в arr[j] записываю число разбитое по 6 бит
                                                                                              //в системе счисления - 2
                    }
                    sb.setLength(0); // очищаю sb
                }
            }

            fout1.close();
            return fout;        */

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
