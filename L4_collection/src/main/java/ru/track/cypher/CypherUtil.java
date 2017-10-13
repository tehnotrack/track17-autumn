package ru.track.cypher;

import java.util.ArrayList;
import java.util.Collections;
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
        Map<Character, Character> cypherTable = new HashMap<>();
        ArrayList<Character> forShuffle = new ArrayList<>();

        for (int i = 0; i < SYMBOLS.length(); i++) {
            forShuffle.add(SYMBOLS.charAt(i));
        }

        Collections.shuffle(forShuffle);

        for (int i = 0; i < SYMBOLS.length(); i++) {
            cypherTable.put(SYMBOLS.charAt(i), forShuffle.get(i));
        }

        return cypherTable;
    }

}