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
        ArrayList<Character> arr = new ArrayList<>();
        for(int i = 0; i < SYMBOLS.length(); i++){
            arr.add(SYMBOLS.charAt(i));
        }
        Collections.shuffle(arr);
        HashMap<Character, Character> dict = new HashMap<>();
        for(int i = 0; i < arr.size(); i++){
            dict.put(SYMBOLS.charAt(i), arr.get(i));
        }
        return dict;
    }

}
