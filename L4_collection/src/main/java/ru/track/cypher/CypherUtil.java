package ru.track.cypher;

import java.io.StringReader;
import java.util.*;

import org.jetbrains.annotations.NotNull;

import javax.print.DocFlavor;

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
        ArrayList<Character> sym = new ArrayList<>();
        HashMap<Character, Character> result = new HashMap<>();
        for (int i = 0; i < SYMBOLS.length(); i++) {
            sym.add(SYMBOLS.charAt(i));
        }

        Collections.shuffle(sym);
        for (int i = 0; i < SYMBOLS.length(); i++) {
            result.put(SYMBOLS.charAt(i), sym.get(i));
        }


        return result;
    }

}
