package ru.track.cypher;

import java.util.*;

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

        Map<Character, Character> result = new TreeMap<>();
        List<Character> mixed = new ArrayList<>();
        for (int counter = 0; counter < SYMBOLS.length(); counter++) {
            mixed.add(SYMBOLS.charAt(counter));
        }

        Collections.shuffle(mixed);

        for (int counter = 0; counter < SYMBOLS.length(); counter++) {
            result.put(SYMBOLS.charAt(counter), mixed.get(counter));
        }
        return result;
    }

}
