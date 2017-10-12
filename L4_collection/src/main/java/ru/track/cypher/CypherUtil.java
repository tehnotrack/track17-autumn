package ru.track.cypher;

import java.util.*;

import org.jetbrains.annotations.NotNull;
import java.util.concurrent.ThreadLocalRandom;

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
        List<Character> values = new LinkedList<>();
        Map<Character, Character> cypher = new HashMap<>();

        for(int i = 0; i < SYMBOLS.length(); i++)
            values.add(SYMBOLS.charAt(i));

        Collections.shuffle(values);

        for(int i = 0; i < SYMBOLS.length(); i++)
            cypher.put(SYMBOLS.charAt(i), values.get(i));

        return cypher;
    }

}
