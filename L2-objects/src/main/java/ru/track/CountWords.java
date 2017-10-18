package ru.track;

import java.io.File;
import java.util.Scanner;


/**
 * Задание 1: Реализовать два метода
 *
 * Формат файла: текстовый, на каждой его строке есть (или/или)
 * - целое число (int)
 * - текстовая строка
 * - пустая строка (пробелы)
 *
 * Числа складываем, строки соединяем через пробел, пустые строки пропускаем
 *
 *
 * Пример файла - words.txt в корне проекта
 *
 * ******************************************************************************************
 *  Пожалуйста, не меняйте сигнатуры методов! (название, аргументы, возвращаемое значение)
 *
 *  Можно дописывать новый код - вспомогательные методы, конструкторы, поля
 *
 * ******************************************************************************************
 *
 */
public class CountWords {

    String skipWord;

    public CountWords(String skipWord) {
        this.skipWord = skipWord;
    }

    /**
     * Метод на вход принимает объект File, изначально сумма = 0
     * Нужно пройти по всем строкам файла, и если в строке стоит целое число,
     * то надо добавить это число к сумме
     * @param file - файл с данными
     * @return - целое число - сумма всех чисел из файла
     */
    public long countNumbers(File file) throws Exception {

        Scanner in = new Scanner(file);
        long count = 0 ;

        while(in.hasNextLine())
        {
            try {
                count += Integer.parseInt(in.nextLine());
            } catch ( Exception e) {}
        }
        in.close();
        return count;
    }


    /**
     * Метод на вход принимает объект File, изначально результат= ""
     * Нужно пройти по всем строкам файла, и если в строка не пустая и не число
     * то надо присоединить ее к результату через пробел
     * @param file - файл с данными
     * @return - результирующая строка
     */
    public String concatWords(File file) throws Exception {
            //Этот спец. объект для построения строки
            StringBuilder sb = new StringBuilder();
            Scanner in = new Scanner(file);

            String strLine = "";
            while (in.hasNextLine()){
                try{
                    strLine = in.nextLine();
                    Integer.parseInt(strLine);
                    continue;
                }
                catch(Exception e)
                {
                }

                if(strLine.equals("")  || strLine.equals(this.skipWord))
                    continue;
                sb.append(strLine);
                sb.append(" ");

            }
            in.close();
            //Возвращаем полученный текст с файла


        return sb.toString();
    }

}

