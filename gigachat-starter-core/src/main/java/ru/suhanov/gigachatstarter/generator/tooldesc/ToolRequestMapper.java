package ru.suhanov.gigachatstarter.generator.tooldesc;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.Map;

public class ToolRequestMapper {

    /**
     * Преобразует Map<String, Object> в объект указанного типа.
     *
     * @param data  Данные в виде Map.
     * @param clazz Класс, в который нужно преобразовать данные.
     * @return Объект типа T.
     */
    public static <T> T mapToObject(Map<String, Object> data, Class<T> clazz) {
        try {
            // Создаем экземпляр объекта
            T instance = createInstance(clazz);

            // Заполняем поля объекта
            for (Field field : clazz.getDeclaredFields()) {
                String fieldName = field.getName();
                if (data.containsKey(fieldName)) {
                    Object value = data.get(fieldName);
                    field.setAccessible(true);

                    // Если значение — это Map, рекурсивно преобразуем его в объект
                    if (value instanceof Map) {
                        field.set(instance, mapToObject((Map<String, Object>) value, field.getType()));
                    } else {
                        field.set(instance, value);
                    }
                }
            }

            return instance;
        } catch (IllegalAccessException | InstantiationException | InvocationTargetException e) {
            throw new RuntimeException("Ошибка при преобразовании Map в объект", e);
        }
    }

    /**
     * Создает экземпляр объекта с использованием конструктора по умолчанию.
     */
    private static <T> T createInstance(Class<T> clazz) throws InstantiationException, IllegalAccessException, InvocationTargetException {
        Constructor<T> constructor = (Constructor<T>) clazz.getDeclaredConstructors()[0];
        constructor.setAccessible(true);
        return constructor.newInstance();
    }
}