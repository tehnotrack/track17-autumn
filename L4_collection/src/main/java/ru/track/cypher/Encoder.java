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

        String encodedString;
        char[] charArray = text.toLowerCase().toCharArray();

        for (int i = 0; i < text.length(); i++){
            charArray[i] = cypherTable.getOrDefault(charArray[i], charArray[i]);
        }

        encodedString = new String(charArray);

        return encodedString;
    }
}
