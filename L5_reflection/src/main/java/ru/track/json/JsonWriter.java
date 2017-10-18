package ru.track.json;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

import com.google.gson.internal.LinkedTreeMap;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import ru.track.json.JsonNullable;
import ru.track.json.SerializedTo;


/**
 * сериализатор в json
 */
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

    /**
     * Используется вспомогательный класс {@link Array}, чтобы работать с object instanceof Array
     * <p>
     * То есть чтобы получить i-й элемент массива, нужно вызвать {@link Array#get(Object, int)}, где i - это число от 0 до {@link Array#getLength(Object)}
     *
     * @param object - который Class.isArray()
     * @return строковое представление массива: [item1, item2, ...]
     */
    @NotNull
    private static String toJsonArray(@NotNull Object object) {
        int length = Array.getLength(object);
        StringBuilder builder = new StringBuilder();

        builder.append("[");

        for(int i=0;i<length;i++) {
            builder.append(toJson(Array.get(object, i)));
            if (i != length - 1) builder.append(",");
        }

        builder.append("]");

        return builder.toString();
    }

    /**
     * В 1 шаг приводится к Collection
     */
    @NotNull
    private static String toJsonCollection(@NotNull Object object) {
        Collection collection = (Collection) object;
        return toJsonArray(collection.toArray());
    }

    /**
     * Сконвертить мап в json. Формат:
     * {key:value, key:value,..}
     * <p>
     * На входе мы проверили, что это Map, можно просто кастовать Map map = (Map) object;
     */
    @NotNull
    private static String toJsonMap(@NotNull Object object) {
        Map map = (Map) object;
        StringBuilder builder = new StringBuilder();

        builder.append("{");

        Map.Entry entry;
        String prefix = "";

        for(Object i : map.entrySet()) {
            entry = (Map.Entry) i;
            Object key = entry.getKey(),
                    value = entry.getValue();

            if (key instanceof Number || key instanceof Integer) {
                key = key.toString();
            }

            builder.append(prefix)
                    .append(toJson(key))
                    .append(":")
                    .append(toJson(value));
            prefix = ",";
        }

        builder.append("}");

        return builder.toString();
        // Можно воспользоваться этим методом, если сохранить все поля в новой мапе уже в строковом представлении
//        return formatObject(stringMap);
    }

    /**
     * 1) Чтобы распечатать объект, нужно знать его внутреннюю структуру, для этого нужно получить его Class-объект:
     * {@link Class} с помощью {@link Object#getClass()}
     * <p>
     * Получить поля класса можно с помощью {@link Class#getDeclaredFields()}
     * Приватные поля недоступны, нужно изменить в рантайм их accessibility: {@link Field#setAccessible(boolean)}
     * <p>
     * 2) Вторая часть задачи: {@link JsonNullable} и {@link SerializedTo}
     * Нужно проверить, что у класса/поля есть аннотация
     * <p>
     * {@link Class#getAnnotation(Class)} / {@link Field#getAnnotation(Class)}
     * и в зависимости от этого изменить поведение
     * <p>
     * NOTE: Удобно сложить все поля объекта в Map<String, String> то етсь {имя поля -> значение поля в json}
     * и воспользоваться методом {@link #formatObject(Map)}
     */
    @NotNull
    private static String toJsonObject(@NotNull Object object) {
        Class clazz = object.getClass();
        Field[] fields = clazz.getDeclaredFields();
        Map<String, String> map = new LinkedTreeMap<>();
        String key, value;

        for(int i=0;i<fields.length;i++) {
            fields[i].setAccessible(true);

            key = String.format("%s", fields[i].getName());
            value = null;

            try {
                if (fields[i].get(object) != null) {
                    value = toJson(fields[i].get(object));
                }
            } catch(IllegalAccessException e) {
                System.out.println("wtf, I made sure it's accessible");
            }

            try {
                if (object.getClass().getAnnotation(JsonNullable.class) != null && fields[i].get(object) == null) {
                    value = "null";
                }
            } catch (IllegalAccessException e) {
                System.out.println("wtf, I made sure it's accessible");
            }

            if (fields[i].getAnnotation(SerializedTo.class) != null) {
                key = fields[i].getAnnotation(SerializedTo.class).value();
            }

            if (value != null) {
                map.put(key, value);
            }
        }


        return formatObject(map);
    }

    /**
     * Вспомогательный метод для форматирования содержимого Map<K, V>
     *
     * @param map
     * @return "{key:value, key:value,..}"
     */
    @NotNull
    private static String formatObject(@NotNull Map<String, String> map) {
        String r = String.join(",", map.entrySet().stream()
                .map(e -> String.format("\"%s\":%s", e.getKey(), e.getValue()))
                .collect(Collectors.toList())
        );

        return String.format("{%s}", r);
    }

}
