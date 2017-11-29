package ru.track.cypher;

import java.util.*;

import org.jetbrains.annotations.NotNull;

public class CypherUtil {

    public static final String SYMBOLS = "abcdefghijklmnopqrstuvwxyz";

    @NotNull
    public static Map<Character, Character> generateCypher() {

        Map<Character, Character> cypherTable = new HashMap<>();
        List<Character> shuffledSymbols = new ArrayList<>();

        for (int i = 0; i < SYMBOLS.length(); i++) {
            shuffledSymbols.add(SYMBOLS.charAt(i));
        }

        Collections.shuffle(shuffledSymbols);

        for (int i = 0; i < SYMBOLS.length(); i++) {
            cypherTable.put(SYMBOLS.charAt(i), shuffledSymbols.get(i));
        }

        return cypherTable;
    }

}
