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

        List<Character> letters = new ArrayList<>();
        for (int i = 0; i < SYMBOLS.length(); i++) {
            letters.add(SYMBOLS.charAt(i));
        }

        Collections.shuffle(letters);

        Map<Character, Character> cypherTable = new HashMap<>();

        for (int i = 0; i < SYMBOLS.length(); i++) {
            cypherTable.put(SYMBOLS.charAt(i), letters.get(i));
        }

        return cypherTable;
    }

}
