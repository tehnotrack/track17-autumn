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
        Map<Character,Character> ch = new HashMap<>();
        List <Character> ar = new ArrayList<>();
        char tmp,temp;
        for (int i = 0; i < SYMBOLS.length(); i++){
            ar.add(SYMBOLS.charAt(i));
        }
        Collections.shuffle(ar);
        //Random r = new Random();
        for (int i = SYMBOLS.length()-1; i >= 0; i--){
            //tmp = ar.get(r.nextInt(i+1));
            ch.put(SYMBOLS.charAt(i),ar.get(i));
//            for(int j = 0; j < ar.size(); j++){
//                temp = ar.get(j);
//                if (temp == tmp){
//                    ar.remove(j);
//                }
//            }
        }
        return ch;
    }

}
