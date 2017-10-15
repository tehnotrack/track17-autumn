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
        text = text.toLowerCase();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < text.length(); ++i) {
            Character c = text.charAt(i);
            if (cypherTable.containsKey(c)) c = cypherTable.get(c);
            sb.append(c);
        }
        return sb.toString();
    }
}
