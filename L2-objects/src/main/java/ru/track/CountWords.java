package ru.track;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
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

    /**
     * Метод на вход принимает объект File, изначально сумма = 0
     * Нужно пройти по всем строкам файла, и если в строке стоит целое число,
     * то надо добавить это число к сумме
     * @param file - файл с данными
     * @return - целое число - сумма всех чисел из файла
     */
    public long countNumbers(File file) throws Exception {
        BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
        String line;
        long result = 0;
        while((line = bufferedReader.readLine()) != null) {
            if(line.trim().equals("")) continue;
            try {
                result += Long.parseLong(line);
            }
            catch(NumberFormatException e) {

            }
        }
        return result;
    }


    /**
     * Метод на вход принимает объект File, изначально результат= ""
     * Нужно пройти по всем строкам файла, и если в строка не пустая и не число
     * то надо присоединить ее к результату через пробел
     * @param file - файл с данными
     * @return - результирующая строка
     */
    public String concatWords(File file) throws Exception {
        StringBuilder stringBuilder = new StringBuilder();
        BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
        String line;
        String resultString = "";
        while((line = bufferedReader.readLine()) != null) {
            if(line.trim().equals("")) continue;
            if(line.equals(skipWord)) continue;
            try {
                int number = Integer.parseInt(line);
            }
            catch(NumberFormatException e) {
                stringBuilder.append(line);
                stringBuilder.append(" ");
            }
        }
        resultString = stringBuilder.toString();
        return resultString;
    }

}

