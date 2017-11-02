package ru.track.cypher;

import java.util.Map;

import org.jetbrains.annotations.NotNull;

/**
 * Класс умеет кодировать сообщение используя шифр
 */
public class Encoder {

    /**
     * Метод шифрует символы текста в соответствие с таблицей
     * NOTE: Текст преводится в lower case!
     *
     * Если таблица: {a -> x, b -> y}
     * то текст aB -> xy, AB -> xy, ab -> xy
     *
     * @param cypherTable - таблица подстановки
     * @param text - исходный текст
     * @return зашифрованный текст
     */
    public String encode(@NotNull Map<Character, Character> cypherTable, @NotNull String text) {
        text = text.toLowerCase();

        StringBuilder textout = new StringBuilder();

        for(int i = 0;i < text.length();i++){
            if(Character.isLetter(text.charAt(i))){
                textout.append(cypherTable.get(text.charAt(i)));
            }else{
                textout.append(text.charAt(i));
            }
        }

        return textout.toString();
    }
}
