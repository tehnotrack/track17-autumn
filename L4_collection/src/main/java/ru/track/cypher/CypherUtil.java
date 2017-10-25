package ru.track.cypher;

import java.util.*;
import java.lang.Integer;

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
        Map<Character, Character> resultMap = new HashMap<Character, Character>();
        ArrayList<Integer> randomIndexes = getRandomIndexes();

        for (int i = 0; i < SYMBOLS.length(); i++) {
            resultMap.put(SYMBOLS.charAt(i), SYMBOLS.charAt(randomIndexes.get(i)));
        }

        return resultMap;
    }

    public static ArrayList<Integer> getRandomIndexes() {
        ArrayList<Integer> randomIndexes = new ArrayList<>();

        for (Integer i = 0; i < SYMBOLS.length(); i++) randomIndexes.add(i);
        Collections.shuffle(randomIndexes);

        return randomIndexes;
    }
}
