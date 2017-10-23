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
     * <p>
     * Если таблица: {a -> x, b -> y}
     * то текст aB -> xy, AB -> xy, ab -> xy
     *
     * @param cypherTable - таблица подстановки
     * @param text        - исходный текст
     * @return зашифрованный текст
     */
    public String encode(@NotNull Map<Character, Character> cypherTable, @NotNull String text) {
        if (text.isEmpty()) {
            return text;
        }
        StringBuilder sb = new StringBuilder(text.toLowerCase());
        for (int i = 0; i < sb.length(); i++) {
            char curChar = sb.charAt(i);
            if (Character.isLetter(curChar)) {
                sb.setCharAt(i, cypherTable.get(curChar));
            }
        }
        return sb.toString();

    }
}
