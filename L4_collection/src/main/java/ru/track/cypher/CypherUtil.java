package ru.track.cypher;

import java.lang.reflect.Array;
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
        Map<Character, Character> ret = new HashMap<>();
        ArrayList<Character> chars = new ArrayList<>();
        for (char c : SYMBOLS.toCharArray()) {
            chars.add(c);
        }
        Collections.shuffle(chars);
        for (int i = 0; i < SYMBOLS.length(); ++i){
            ret.put(SYMBOLS.charAt(i), chars.get(i));
        }
        return ret;
    }

}
