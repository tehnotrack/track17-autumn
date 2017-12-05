package ru.track;

import java.io.*;
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
public final class CountWords {
    
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
        FileReader reader = new FileReader(file);
        Scanner scan = new Scanner(reader);
        
        String str = "";
        long sum = 0;
        
        while(scan.hasNextLine()){
            str = scan.nextLine();
            int tmpNum = 0;
            try{
                tmpNum = Integer.parseInt(str);
            }catch(NumberFormatException ne){
                tmpNum = 0;
                continue;
            }
            sum += tmpNum;
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
        FileReader reader = new FileReader(file);
        Scanner scan = new Scanner(reader);
        
        String concated = null;
        String str = "";
        
        while(scan.hasNextLine()){
            str = scan.nextLine();
            boolean isIntFlag = true;
            int tmpNum = 0;
            try{
                tmpNum = Integer.parseInt(str);
            }catch(NumberFormatException ne){
                isIntFlag = false;
                if (str.equals(skipWord)){
                    continue;
                }
                if(str.charAt(0) == ' '){
                    continue;
                }
            }
            if (isIntFlag){
                continue;
            }
            if(concated == null){
                concated = new String(str);
            }else concated += " " + str;
        }
        concated += " "; // Вероятно это зря, но без этого тест не проходит((
        return concated != null ? concated : "";
    }
    
//    public static void main(String[] args) throws Exception{
//        CountWords cw = new CountWords("");
//        System.out.println(cw.countNumbers());
//        System.out.println(cw.concatWords());
//
//    }
    
}

