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
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);
            Character ch = cypherTable.get(c);
            //если встретили символ, равный ключу мапы, то записываем его зашифрованным
            if (ch != null) {
                sb.append(cypherTable.get(c));
            } else {
                //иначе просто записываем не содержащийся в ключах мапы символ
                sb.append(c);
            }
        }
        return sb.toString();
    }
}