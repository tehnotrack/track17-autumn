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

        StringBuilder sb = new StringBuilder();
        char tmpChar;

        text = text.toLowerCase();

        for (int i = 0; i < text.length(); i++)
        {
            tmpChar = Character.toLowerCase(text.charAt(i));
            if ( tmpChar >= 'a' && tmpChar <= 'z')
            {
                sb.append(cypherTable.get(tmpChar));
            }
            else
            {
                sb.append(text.charAt(i));
            }
        }
        return sb.toString();
    }

}
