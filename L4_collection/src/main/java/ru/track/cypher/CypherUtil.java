package ru.track.cypher;

import java.util.HashMap;
import java.util.Map;

import org.jetbrains.annotations.NotNull;

/**
 * Вспомогательные методы шифрования/дешифрования
 */
public class CypherUtil {

    public static final String SYMBOLS = "abcdefghijklmnopqrstuvwxyz";

    /**
     * Генерирует таблицу подстановки - то есть каждой буква алфавита ставится в соответствие другая буква
     * Не должно быть пересечений (a -> x, b -> x). Маппинг уникальный
     *
     * @return таблицу подстановки шифра
     */
    @NotNull
    public static Map<Character, Character> generateCypher() {
        Map<Character, Character> result = new HashMap<>();

        int symbolsLength = SYMBOLS.length();
        for (int i = 0; i < symbolsLength; ++i) {
            result.put(SYMBOLS.charAt(i), SYMBOLS.charAt(symbolsLength - i - 1));
        }

        return result;

    }

}
