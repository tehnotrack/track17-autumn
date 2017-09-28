package ru.track;

import java.io.*;


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
    
    static BufferedReader openFile(File file) throws FileNotFoundException {
        String fileName = file.getName();
        
        FileInputStream fstream = new FileInputStream(fileName);
        BufferedReader reader = new BufferedReader(new InputStreamReader(fstream));
        
        return reader;
    }
    
    /**
     * Метод на вход принимает объект File, изначально сумма = 0
     * Нужно пройти по всем строкам файла, и если в строке стоит целое число,
     * то надо добавить это число к сумме
     * @param file - файл с данными
     * @return - целое число - сумма всех чисел из файла
     */
    public long countNumbers(File file) throws Exception {
        BufferedReader reader = openFile(file);
        String line;
        long number;
        long sum = 0;
        
        while ((line = reader.readLine()) != null) {
            try{
                number = Long.parseLong(line);
            }
            catch (NumberFormatException exception){
                number = 0;
            }
            
            sum += number;
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
        BufferedReader reader = openFile(file);
        String line;
        StringBuilder sum = new StringBuilder();
        
        while ((line = reader.readLine()) != null){
            try{
                Long.parseLong(line);
            }
            catch (NumberFormatException exception){
                if (!line.isEmpty() && !line.equals(skipWord)) {
                    sum.append(line + " ");
                }
            }
        }
        
        return sum.toString();
    }
    
}

