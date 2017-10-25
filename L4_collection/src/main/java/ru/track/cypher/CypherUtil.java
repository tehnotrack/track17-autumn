package ru.track.cypher;

import java.util.*;
import java.util.stream.Collectors;

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
        List<Character> alph = SYMBOLS.chars().mapToObj((i) -> Character.valueOf((char)i)).collect(Collectors.toList());
        Collections.shuffle(alph);
        Map<Character, Character> cypher = new HashMap<>();
        for (int i = 0; i < alph.size(); i++) {
            cypher.put(SYMBOLS.charAt(i), alph.get(i));
        }
        return  cypher;
    }

}
