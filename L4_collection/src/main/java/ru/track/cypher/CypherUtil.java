package ru.track.cypher;

import org.jetbrains.annotations.NotNull;

import java.util.*;

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
        List<Character> symbols = new ArrayList<>();
        for (int i = 0; i < SYMBOLS.length(); i++) {
            symbols.add(SYMBOLS.charAt(i));
        }

        Collections.shuffle(symbols);

        for(int i = 0; i < SYMBOLS.length(); i++) {
            cypher.put(SYMBOLS.charAt(i), symbols.get(i));
        }

        return cypher;
    }
}