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
        char[] alphabet = SYMBOLS.toCharArray();
        List<Character> list = new ArrayList<>();
        for (char c : alphabet) {
            list.add(c);
        }

        Collections.shuffle(list);

        Map<Character, Character> map = new HashMap<Character, Character>() {{
            for (int i = 0; SYMBOLS.length() > i; i++)
                put(alphabet[i], list.get(i));
        }};

        return map;
    }

}
