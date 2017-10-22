package ru.track.cypher;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

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
        Map<Character, Character> cypher = new HashMap<>();
        char[] chars = SYMBOLS.toCharArray();
        //a -> b, b -> c, ..., y -> z, z -> a
        for (int i = 0; i < chars.length - 1; i++) {
            cypher.put(chars[i], chars[i + 1]);
        }
        cypher.put('z', 'a');
        return cypher;
    }
}