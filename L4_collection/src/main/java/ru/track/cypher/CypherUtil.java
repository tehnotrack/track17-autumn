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
        Map<Character, Character> res =  new HashMap<>();
        Character c;

        Stack<Character> pool  = new Stack<>(); // might want to optimise
        for (int i=0;i < SYMBOLS.length();i++) {
            pool.push(SYMBOLS.charAt(i));
        }

        Collections.shuffle(pool);

        for(int i=0;i<SYMBOLS.length();i++) {
            c = SYMBOLS.charAt(i);
            res.put(c, pool.pop());
        }

        return res;
    }

}
