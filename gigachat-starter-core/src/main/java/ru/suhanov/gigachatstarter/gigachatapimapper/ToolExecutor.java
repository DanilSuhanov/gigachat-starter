package ru.suhanov.gigachatstarter.gigachatapimapper;

import ru.suhanov.dto.ai.gigachat.MessagesResFunctionCall;
import ru.suhanov.gigachatstarter.gigachatapimapper.tooldesc.annotation.Tool;
import org.springframework.stereotype.Service;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.HashMap;
import java.util.Map;

@Service
public class ToolExecutor {

    private final Map<String, Method> toolMethods = new HashMap<>();
    private final Map<String, Object> toolInstances = new HashMap<>();

    /**
     * Регистрирует класс с методами, помеченными аннотацией @Tool.
     *
     * @param clazz Класс, содержащий методы с аннотацией @Tool.
     */
    public void registerToolClass(Class<?> clazz) {
        try {
            Object instance = clazz.getDeclaredConstructor().newInstance();
            for (Method method : clazz.getDeclaredMethods()) {
                if (method.isAnnotationPresent(Tool.class)) {
                    Tool toolAnnotation = method.getAnnotation(Tool.class);
                    toolMethods.put(toolAnnotation.name(), method);
                    toolInstances.put(toolAnnotation.name(), instance);
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("Ошибка при регистрации класса с инструментами", e);
        }
    }

    /**
     * Выполняет метод, соответствующий имени функции в MessagesResFunctionCall.
     *
     * @param functionCall Объект с именем функции и аргументами.
     * @return Результат выполнения метода.
     */
    public Object execute(MessagesResFunctionCall functionCall) {
        String functionName = functionCall.getName();
        Object arguments = functionCall.getArguments();

        if (!toolMethods.containsKey(functionName)) {
            throw new IllegalArgumentException("Метод с именем " + functionName + " не найден");
        }

        Method method = toolMethods.get(functionName);
        Object instance = toolInstances.get(functionName);

        try {
            // Преобразуем аргументы в Map<String, Object>
            Map<String, Object> argsMap = (Map<String, Object>) arguments;

            // Преобразуем Map в массив объектов для вызова метода
            Object[] methodArgs = prepareMethodArguments(method, argsMap);

            // Вызываем метод
            return method.invoke(instance, methodArgs);
        } catch (Exception e) {
            throw new RuntimeException("Ошибка при выполнении метода " + functionName, e);
        }
    }

    /**
     * Подготавливает аргументы для вызова метода.
     *
     * @param method Метод, который будет вызван.
     * @param argsMap Аргументы в виде Map.
     * @return Массив объектов для вызова метода.
     */
    private Object[] prepareMethodArguments(Method method, Map<String, Object> argsMap) {
        Parameter[] parameters = method.getParameters();
        Object[] methodArgs = new Object[parameters.length];

        for (int i = 0; i < parameters.length; i++) {
            String paramName = parameters[i].getName();
            Class<?> paramType = parameters[i].getType();

            if (argsMap.containsKey(paramName)) {
                Object argValue = argsMap.get(paramName);

                // Если аргумент — это Map, преобразуем его в объект
                if (argValue instanceof Map) {
                    methodArgs[i] = mapToObject((Map<String, Object>) argValue, paramType);
                } else {
                    methodArgs[i] = argValue;
                }
            } else {
                throw new IllegalArgumentException("Аргумент " + paramName + " не найден");
            }
        }

        return methodArgs;
    }

    /**
     * Преобразует Map в объект указанного типа.
     *
     * @param data  Данные в виде Map.
     * @param clazz Класс, в который нужно преобразовать данные.
     * @return Объект типа T.
     */
    private <T> T mapToObject(Map<String, Object> data, Class<T> clazz) {
        try {
            T instance = clazz.getDeclaredConstructor().newInstance();
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
        } catch (Exception e) {
            throw new RuntimeException("Ошибка при преобразовании Map в объект", e);
        }
    }
}