package ru.track;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import static java.nio.charset.StandardCharsets.UTF_8;


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
        int i = 0;
        long result = 0;
        String path = file.getAbsolutePath();
        List<String> lines = Files.readAllLines(Paths.get(path), StandardCharsets.UTF_8);
        for(String line : lines){
            if (!line.equals(this.skipWord))
                try {
                    result += Integer.parseInt(line);
                } catch (NumberFormatException e) {

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
        StringBuilder builder = new StringBuilder();
        String path = file.getAbsolutePath();
        List<String> lines = Files.readAllLines(Paths.get(path), StandardCharsets.UTF_8);
        for(String line : lines){
            if (!line.equals(this.skipWord))
                try {
                    Integer.parseInt(line);
                } catch (NumberFormatException e) {
                    builder.append(line);
                    builder.append(" ");
                }
        }
        String result = builder.toString();
        return result;
    }

}

