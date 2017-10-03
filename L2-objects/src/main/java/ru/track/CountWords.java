package ru.track;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


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
        long n = 0;
        BufferedReader in = new BufferedReader(new FileReader(file));
        String s;
        while ((s = in.readLine()) != null){
            try {
                n += Integer.parseInt(s);
            }
            catch (NumberFormatException e)
            {}
            }
        return n;
    }


    /**
     * Метод на вход принимает объект File, изначально результат= ""
     * Нужно пройти по всем строкам файла, и если в строка не пустая и не число
     * то надо присоединить ее к результату через пробел
     * @param file - файл с данными
     * @return - результирующая строка
     */
    public String concatWords(File file) throws Exception {
        BufferedReader in = new BufferedReader(new FileReader(file));
        String s;
        StringBuilder s1 = new StringBuilder();
        while ((s = in.readLine()) != null){
           if ((!s.equals("")) & (!s.equals(skipWord)))
               try {
                Integer.parseInt(s);
                }
             catch (NumberFormatException e)
                {
                 s1.append(s + " ");
                }
        }
        return s1.toString();
    }

    public static void main(String args[])
    {

    }

}

