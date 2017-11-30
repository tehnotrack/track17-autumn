package ru.track.json;

import java.lang.annotation.Annotation;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.util.*;
import java.util.stream.Collectors;

import jdk.nashorn.internal.runtime.JSONListAdapter;
import jdk.nashorn.internal.runtime.arrays.ArrayIndex;
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
     * То есть чтобы получить i-й элемент массива, нужно вызвать {@link Array#get(Object, int)}, где i - это число от 0 до {@link Array#getLength(Object)}
     *
     * @param object - который Class.isArray()
     * @return строковое представление массива: [item1, item2, ...]
     */
    @NotNull
    private static String toJsonArray(@NotNull Object object) {
        StringBuilder outStr = new StringBuilder();
        int arrlength = Array.getLength(object);
        if (arrlength == 0) {
            return outStr.toString();
        }
        outStr.append('[');
        for(int i=0; i < arrlength; i++)
        {
            outStr.append(toJson(Array.get( object,i)));
            outStr.append(',');
        }
        outStr.deleteCharAt(outStr.length()-1);
        outStr.append(']');

        return outStr.toString();
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
        Map mmap = (Map) object;
        StringBuilder outStr = new StringBuilder();
        if (mmap.size() == 0)
            return outStr.toString();
        outStr.append('{');
        Object key;
        for (Object elem : mmap.entrySet()) {
            Map.Entry entry = (Map.Entry)elem;
            key = entry.getKey();
            if (!(key instanceof String)) {
                outStr.append('"');
            }
            outStr.append(toJson(key));
            if (!(key instanceof String)) {
                outStr.append('"');
            }
            outStr.append(':');
            outStr.append(toJson(entry.getValue()));
            outStr.append(',');
        }
        outStr.deleteCharAt(outStr.length()-1);
        outStr.append('}');
        return outStr.toString();
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
        LinkedHashMap<String, String> mmap = new LinkedHashMap<>();
        Field[] declaredFields = clazz.getDeclaredFields();
        String lhs;
        String rhs;

        for (Field field : declaredFields) {
            field.setAccessible(true);

            lhs = field.getName();
            try {
                rhs = toJson(field.get(object));
            } catch (IllegalAccessException e) {
                continue;
            }
            if (field.isAnnotationPresent(SerializedTo.class)) {
                lhs = (field.getAnnotation(SerializedTo.class)).value();
            }

            if (rhs.equals("null")){
                if(clazz.isAnnotationPresent(JsonNullable.class) ||
                        field.isAnnotationPresent(JsonNullable.class)) {
                    mmap.put(lhs, rhs);
                }
            }
            else{
                mmap.put(lhs, rhs);
            }
        }

        return formatObject(mmap);
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