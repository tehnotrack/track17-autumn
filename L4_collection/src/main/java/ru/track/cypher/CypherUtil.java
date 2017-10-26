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

        Random rm=new Random();
        Map<Character,Character> result=new HashMap<>();
        List<Character> list= new ArrayList<>();
        for (int i=0;i<SYMBOLS.length();i++){
            list.add(SYMBOLS.charAt(i));
        }
        Collections.shuffle(list);

        for (int i=0;i<SYMBOLS.length();i++){
            result.put(SYMBOLS.charAt(i),list.get(i));
        }

        return result;
    }

}
