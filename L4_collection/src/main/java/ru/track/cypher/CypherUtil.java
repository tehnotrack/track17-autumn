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
        Map<Character, Character> myAlp = new HashMap<>();
        List<Character> myLst = new ArrayList<>();

        for (int i = 0; i < SYMBOLS.length(); i++) {
            myLst.add(SYMBOLS.charAt(i));
        }

        Collections.shuffle(myLst);

        for (int i = 0; i < SYMBOLS.length(); i++)
            myAlp.put(SYMBOLS.charAt(i), myLst.get(i));

        return myAlp;
    }

}