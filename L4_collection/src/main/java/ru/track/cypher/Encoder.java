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
        StringBuilder encodedText = new StringBuilder();
        for (char symbol : text.toLowerCase().toCharArray()) {
            Character encodedSymbol = cypherTable.get(symbol);
            if (encodedSymbol != null) {
                encodedText.append(encodedSymbol);
            } else {
                encodedText.append(symbol);
            }
        }
        return encodedText.toString();
    }
}
