package ru.track.cypher;

import java.util.*;

import org.jetbrains.annotations.NotNull;

/**
 * Вспомогательные методы шифрования/дешифрования
 */
public class CypherUtil
{

    public static final String SYMBOLS = "abcdefghijklmnopqrstuvwxyz";

    /**
     * Генерирует таблицу подстановки - то есть каждой буква алфавита ставится в соответствие другая буква
     * Не должно быть пересечений (a -> x, b -> x). Маппинг уникальный
     *
     * @return таблицу подстановки шифра
     */
    @NotNull
    public static Map<Character, Character> generateCypher()
    {
        Map<Character, Character> res = new HashMap<>();

        List<Character> shuffled = new ArrayList<Character>();
        for (int i = 0; i < SYMBOLS.length(); ++i) shuffled.add(SYMBOLS.charAt(i));
        Collections.shuffle(shuffled);
        for (int i = 0; i < SYMBOLS.length(); ++i) res.put(SYMBOLS.charAt(i), shuffled.get(i));
        return res;
    }

}
