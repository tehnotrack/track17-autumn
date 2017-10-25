package ru.track.cypher;

import java.util.Map;

import org.jetbrains.annotations.NotNull;

/**
 * Класс умеет кодировать сообщение используя шифр
 */
public class Encoder {

    /**
     * Метод шифрует символы текста в соответствие с таблицей
     * NOTE: Текст преводится в lower case!
     *
     * Если таблица: {a -> x, b -> y}
     * то текст aB -> xy, AB -> xy, ab -> xy
     *
     * @param cypherTable - таблица подстановки
     * @param text - исходный текст
     * @return зашифрованный текст
     */
    public String encode(@NotNull Map<Character, Character> cypherTable, @NotNull String text) {
        String mytext= new String();
        StringBuilder encodedText = new StringBuilder();
        mytext = text.toLowerCase();
        for (int i=0; i<mytext.length(); ++i){
            if(Character.isLetter(mytext.charAt(i))) {
                encodedText.append(cypherTable.get(mytext.charAt(i)));
            }
            else {
                encodedText.append(mytext.charAt(i));
            }
        }
        return encodedText.toString();
    }
}
