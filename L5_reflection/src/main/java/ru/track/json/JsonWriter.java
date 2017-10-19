package ru.track.json;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.util.*;
import java.util.stream.Collectors;

import com.sun.org.apache.xpath.internal.operations.Bool;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;


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
     * То есть чтобы получить i-й элемент массива, нужно вызвать {@link Array#get(Object, int)},
     * где i - это число от 0 до {@link Array#getLength(Object)}
     *
     * @param object - который Class.isArray()
     * @return строковое представление массива: [item1, item2, ...]
     */
    @NotNull
    private static String toJsonArray(@NotNull Object object) {
        int length = Array.getLength(object);
        // TODO: implement!
        StringBuilder res = new StringBuilder();
        res.append("[");
        for (int i = 0; i < length; i++) {
            Object val = Array.get(object, i);
            res.append(JsonWriter.toJson(val));
            if (i < length - 1) {
                res.append(",");
            }
        }
        res.append("]");
        return res.toString();
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
        // TODO: implement!
        Map map = (Map) object;
        StringBuilder res = new StringBuilder();
        res.append("{");
        final boolean[] isFirst = {true};
        map.forEach((k,v) -> {
            if (!isFirst[0]) {
                res.append(",");
            }
            isFirst[0] = false;

            // some problems with String and "key"-form
            Class clazz = k.getClass();
            if (clazz.equals(String.class)
                    || clazz.equals(Character.class)
                    || clazz.isEnum()
                    ) {
                res.append(String.format("\"%s\":", k));
            } else {
                res.append("\"" + JsonWriter.toJson(k) + "\"" + ":");
            }
            res.append( JsonWriter.toJson(v));
        });
        res.append("}");
        return res.toString();
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
        // TODO: implement!
        Map<String, String> map = new LinkedHashMap<>();
        Class clazz = object.getClass();
        Field[] declaredFields = clazz.getDeclaredFields();

        for (Field f : declaredFields) {
            String name = f.getName();
            f.setAccessible(true);
            Object val;
            try {
                val = f.get(object);
                if (val != null){
                    map.put(name, JsonWriter.toJson(val));
                }
            } catch (Exception e) {
                e.printStackTrace();
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
