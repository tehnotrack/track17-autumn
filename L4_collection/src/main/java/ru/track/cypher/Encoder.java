package ru.track.cypher;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
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

        if (text.length() == 0)
            return text;

        Character ch;

        StringBuilder encoded = new StringBuilder();

        for (int i = 0; i < text.length(); i++) {
            ch = cypherTable.get(Character.toLowerCase(text.charAt(i)));
            if (ch == null)
                encoded.append(text.charAt(i));
            else
                encoded.append(ch.charValue());
        }

        return encoded.toString();
    }
}
