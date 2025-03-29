package ru.suhanov.gigachatstarter.gigachatapiservice.toolexecutor;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import ru.suhanov.dto.ai.gigachat.MessagesResFunctionCall;
import org.springframework.stereotype.Service;
import ru.suhanov.gigachatstarter.gigachatapiservice.toolwrapper.toolProvider.AvailableForToolParse;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public abstract class ToolExecutor {

    private final Map<String, Method> toolMethods = new HashMap<>();
    private final Map<String, Object> toolInstances = new HashMap<>();

    protected final List<AvailableForToolParse> toolClasses;

    @PostConstruct
    public void loadTool() {
        toolClasses.forEach(toolClass -> registerToolClass(toolClass.getClass()));
    }

    /**
     * Регистрирует класс с методами, помеченными аннотацией @Tool.
     *
     * @param clazz Класс, содержащий методы с аннотацией @Tool.
     */
    public void registerToolClass(Class<?> clazz) {
        try {
            Object instance = clazz.getDeclaredConstructor().newInstance();
            for (Method method : clazz.getDeclaredMethods()) {
                getToolNameIfMethodIsTool(method).ifPresent(toolName -> {
                    toolMethods.put(toolName, method);
                    toolInstances.put(toolName, instance);
                });
            }
        } catch (Exception e) {
            throw new RuntimeException("Ошибка при регистрации класса с инструментами", e);
        }
    }

    protected abstract Optional<String> getToolNameIfMethodIsTool(Method method);

    /**
     * Выполняет метод, соответствующий имени функции в MessagesResFunctionCall.
     *
     * @param functionCall Объект с именем функции и аргументами.
     * @return Результат выполнения метода.
     */
    public Object execute(MessagesResFunctionCall functionCall) {
        String functionName = getMethodName(functionCall);
        Object arguments = functionCall.getArguments();

        if (!toolMethods.containsKey(functionName)) {
            throw new IllegalArgumentException("Метод с именем " + functionName + " не найден");
        }

        return execute(functionName, (Map<String, Object>) arguments);
    }

    protected Object execute(String functionName, Map<String, Object> arguments) {
        try {
            Method method = toolMethods.get(functionName);
            Object instance = toolInstances.get(functionName);

            // Преобразуем Map в массив объектов для вызова метода
            Object[] methodArgs = prepareMethodArguments(
                    method,
                    arguments
            );

            // Вызываем метод
            return method.invoke(instance, methodArgs);
        } catch (Exception e) {
            throw new RuntimeException("Ошибка при выполнении метода " + functionName, e);
        }
    }

    protected abstract String getMethodName(MessagesResFunctionCall functionCall);

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