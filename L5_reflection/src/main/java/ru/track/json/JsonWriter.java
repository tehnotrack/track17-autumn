package ru.track.json;

import java.lang.annotation.Annotation;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

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
        int length = Array.getLength(object);
        StringBuilder str = new StringBuilder();

        str.append('[');
        for (int i = 0; i < length; i++)
        {
            str.append(toJson(Array.get(object, i)));

            if (i != (length -1) ){
                str.append(",");}
            else{
                str.append("]");
            }
        }


        return str.toString();
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
        Map<Object, Object> map = (Map) object;
        Map<String, String> stringMap = new LinkedHashMap<>();

        for(Map.Entry<Object, Object> entry : map.entrySet())
        {
            if (entry.getKey().getClass() != String.class)
                stringMap.put(toJson(entry.getKey()), toJson(entry.getValue()));
            else
                stringMap.put((String)entry.getKey(), toJson(entry.getValue()));
        }

        return formatObject(stringMap);
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
        JsonNullable js = (JsonNullable) clazz.getAnnotation(JsonNullable.class);
        Field[] fields = clazz.getDeclaredFields();
        Field.setAccessible(fields, true);
        Map<String, String> mp = new LinkedHashMap<>();

        for(Field field: fields)
        {
            field.setAccessible(true);
            SerializedTo serializedTo = field.getAnnotation(SerializedTo.class);
            if (serializedTo != null)
                try {
                if (js != null)
                    mp.put(serializedTo.value(), toJson(field.get(object)));
                else if (field.get(object) != null)
                    mp.put(serializedTo.value(), toJson(field.get(object)));
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            else
                try {
                    if (js != null)
                        mp.put(field.getName(), toJson(field.get(object)));
                    else if (field.get(object) != null)
                        mp.put(field.getName(), toJson(field.get(object)));
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }

        }
        return formatObject(mp);
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
