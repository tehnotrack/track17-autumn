package ru.track.cypher;

import java.util.Map;

import org.jetbrains.annotations.NotNull;

public class Encoder {

    public String encode(@NotNull Map<Character, Character> cypherTable, @NotNull String text) {

        StringBuilder encodeStringBuilder = new StringBuilder();


        for (int i = 0; i < text.length(); i++) {

            char encodeChar = text.toLowerCase().charAt(i);

            if (Character.isLetter(encodeChar)) {

                encodeStringBuilder.append(cypherTable.get(encodeChar));
            } else {

                encodeStringBuilder.append(text.charAt(i));
            }
        }

        return encodeStringBuilder.toString();
    }
}
