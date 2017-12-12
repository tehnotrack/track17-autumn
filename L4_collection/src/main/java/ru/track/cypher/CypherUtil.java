package ru.track.cypher;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

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

        HashMap <Character, Character> cypher = new HashMap<>();

        final Random random = new Random();

        for (char c = 'a'; c != 'z' + 1; c++){
            int rndChar = random.nextInt('z'-'a' + 1);

            if (!cypher.containsValue((char)(rndChar + 'a'))){
                cypher.put(c, (char)(rndChar + 'a'));
            }else {
                c--;
            }
        }
        return cypher;
    }
}
