package ru.track.json;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class JsonWriter {

    // В зависимости от типа объекта вызывает соответствующий способ сериализации
    public static String toJson(@Nullable Object object) {
        if (object == null) {
            return "null";
        }

        Class clazz = object.getClass();

        if (clazz.equals(String.class)
                || clazz.equals(Character.class)
                || clazz.isEnum()
                ) {
            return String.format("\"%s\"", object);
        }

        if (object instanceof Boolean || object instanceof Number) {
            return object.toString();
        }

        if (clazz.isArray()) {
            return toJsonArray(object);
        }

        if (object instanceof Collection) {
            return toJsonCollection(object);
        }

        if (object instanceof Map) {
            return toJsonMap(object);
        }

        return toJsonObject(object);
    }

    @NotNull
    private static String toJsonArray(@NotNull Object object) {
        int length = Array.getLength(object);

        StringBuilder jsonArrayString = new StringBuilder();
        jsonArrayString.append("[");

        for (int i = 0; i < length-1; i++) {
            jsonArrayString.append(toJson(Array.get(object, i))).append(",");
        }

        jsonArrayString.append(toJson(Array.get(object, length - 1))).append("]");

        return jsonArrayString.toString();
    }

    @NotNull
    private static String toJsonCollection(@NotNull Object object) {
        Collection collection = (Collection) object;
        return toJsonArray(collection.toArray());
    }

    @NotNull
    private static String toJsonMap(@NotNull Object object) {
        Map<?, ?> map = (Map) object;
        Map<String, String> jsonStringMap = new LinkedHashMap<>();

        for (Map.Entry entry : map.entrySet()) {
            jsonStringMap.put(entry.getKey().toString(), toJson(entry.getValue()));
        }

        return formatObject(jsonStringMap);
    }

    @NotNull
    private static String toJsonObject(@NotNull Object object) {
        Class clazz = object.getClass();

        Field[] fields = clazz.getDeclaredFields();
        boolean isNullable = clazz.getAnnotation(JsonNullable.class) != null;
        Map<String, String> stringMap = new LinkedHashMap<>();

        for (Field field : fields) {
            field.setAccessible(true);

            String name = field.getName();
            SerializedTo serializedTo = field.getAnnotation(SerializedTo.class);
            name = (serializedTo == null) ? name : serializedTo.value();
            try {
                Object value = field.get(object);
                if (value != null || isNullable) {
                    stringMap.put(name, toJson(value));
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }

        return formatObject(stringMap);
    }

    @NotNull
    private static String formatObject(@NotNull Map<String, String> map) {
        String r = String.join(",", map.entrySet().stream()
                .map(e -> String.format("\"%s\":%s", e.getKey(), e.getValue()))
                .collect(Collectors.toList())
        );

        return String.format("{%s}", r);
    }

}
