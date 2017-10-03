package ru.track;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;


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

    public long countNumbers(File file) throws Exception {

        long sum = 0;

        FileReader fr = new FileReader (file);
        BufferedReader reader = new BufferedReader(fr);

        String line;
        while ((line = reader.readLine()) != null) {
            try {
                sum += Integer.parseInt(line);
            }
            catch (NumberFormatException e) {
            }
        }
        return sum;
    }



    /**
     * Метод на вход принимает объект File, изначально результат= ""
     * Нужно пройти по всем строкам файла, и если в строка не пустая и не число
     * то надо присоединить ее к результату через пробел
     * @param file - файл с данными
     * @return - результирующая строка
     */
    public String concatWords(File file) throws Exception {

        FileReader fr = new FileReader (file);
        BufferedReader reader = new BufferedReader(fr);

        String line;
        StringBuilder myStr = new StringBuilder();
        while ((line = reader.readLine()) != null)
        {
            if (!line.equals(skipWord) & !line.isEmpty()) {
                try {
                    Integer.parseInt(line);
                } catch (NumberFormatException e) {
                    myStr.append(line);
                    myStr.append(" ");
                }
            }
        }


        return myStr.toString();
    }

}

