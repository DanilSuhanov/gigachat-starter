package ru.suhanov.gigachatstarter.gigachatapimapper.tooldesc.toolfinder;

import ru.suhanov.dto.ai.gigachat.ChatFunctionsInner;
import ru.suhanov.gigachatstarter.gigachatapimapper.tooldesc.annotation.AllowedValues;
import ru.suhanov.gigachatstarter.gigachatapimapper.tooldesc.annotation.Description;
import ru.suhanov.gigachatstarter.gigachatapimapper.tooldesc.annotation.Required;

import java.lang.reflect.*;
import java.util.*;

public abstract class ToolSpecFinder {

    /**
     * Находит все методы, помеченные аннотацией @Tool, и возвращает их описание.
     *
     * @param clazz Класс, в котором нужно искать методы.
     * @return Список описаний методов.
     */
    public List<ChatFunctionsInner> getToolSpecs(Class<?> clazz) {
        List<ChatFunctionsInner> tools = new ArrayList<>();

        for (Method method : clazz.getDeclaredMethods()) {
            handleMethod(method).ifPresent(tools::add);
        }

        return tools;
    }

    protected abstract Optional<ChatFunctionsInner> handleMethod(Method method);

    /**
     * Генерирует схему для параметров метода.
     */
    protected Map<String, Object> generateSchemaForParameters(Method method) {
        Map<String, Object> schema = new HashMap<>();
        schema.put("type", "object");

        Map<String, Object> properties = new HashMap<>();
        List<String> required = new ArrayList<>();

        for (Parameter param : method.getParameters()) {
            Class<?> paramType = param.getType();

            Map<String, Object> paramSchema = generateSchemaForType(paramType);

            hadnleParameter(param, paramSchema, required, method.getName());

            properties.put(param.getName(), paramSchema);
        }

        schema.put("properties", properties);
        if (!required.isEmpty()) {
            schema.put("required", required);
        }

        return schema;
    }

    protected abstract void hadnleParameter(Parameter param, Map<String, Object> paramSchema, List<String> required, String methodName);

    /**
     * Генерирует схему для возвращаемого типа.
     */
    protected Map<String, Object> generateSchemaForReturnType(Method method) {
        Map<String, Object> schema = generateSchemaForType(method.getReturnType());

        // Добавляем обязательные поля для возвращаемого типа
        if (schema.containsKey("required")) {
            List<String> required = (List<String>) schema.get("required");
            if (!required.isEmpty()) {
                schema.put("required", required);
            }
        }

        return schema;
    }

    /**
     * Универсальный метод генерации схемы для любого типа.
     */
    protected Map<String, Object> generateSchemaForType(Class<?> type) {
        Map<String, Object> schema = new HashMap<>();

        if (isSimpleType(type)) {
            // Простые типы (String, int, boolean и т.д.)
            schema.put("type", getJsonType(type));
        } else if (type.isArray() || List.class.isAssignableFrom(type)) {
            // Обработка массивов и списков
            schema.put("type", "array");
            Class<?> componentType = type.isArray() ? type.getComponentType() : getGenericType(type);
            schema.put("items", generateSchemaForType(componentType));
        } else if (Map.class.isAssignableFrom(type)) {
            // Обработка словарей (Map)
            schema.put("type", "object");
            Class<?> valueType = getGenericType(type);
            schema.put("additionalProperties", generateSchemaForType(valueType));
        } else {
            // Сложные типы (объекты)
            schema.put("type", "object");
            Map<String, Object> properties = new HashMap<>();
            List<String> required = new ArrayList<>();

            for (Field field : type.getDeclaredFields()) {
                String fieldName = field.getName();
                Class<?> fieldType = field.getType();

                Map<String, Object> fieldSchema = generateSchemaForType(fieldType);

                handleReturnField(field, fieldSchema, required, fieldName);

                properties.put(fieldName, fieldSchema);
            }

            schema.put("properties", properties);
            if (!required.isEmpty()) {
                schema.put("required", required);
            }
        }

        return schema;
    }

    protected abstract void handleReturnField(Field field, Map<String, Object> fieldSchema, List<String> required, String fieldName);

    /**
     * Получает тип элемента для списка или словаря.
     */
    private Class<?> getGenericType(Class<?> type) {
        if (type.isArray()) {
            return type.getComponentType();
        } else if (List.class.isAssignableFrom(type)) {
            // Для списков возвращаем Object.class, если не удалось определить тип
            return Object.class;
        } else if (Map.class.isAssignableFrom(type)) {
            // Для словарей возвращаем тип значения (Object.class по умолчанию)
            return Object.class;
        }
        return Object.class;
    }

    /**
     * Проверяет, является ли тип простым.
     */
    private boolean isSimpleType(Class<?> type) {
        return type.isPrimitive() ||
                type == String.class ||
                Number.class.isAssignableFrom(type) ||
                type == Boolean.class;
    }

    /**
     * Возвращает JSON-тип для простых типов.
     */
    private String getJsonType(Class<?> type) {
        if (type == String.class) return "string";
        if (type == boolean.class || type == Boolean.class) return "boolean";
        if (type.isPrimitive() || Number.class.isAssignableFrom(type)) {
            return type == float.class || type == double.class ? "number" : "integer";
        }
        return "object";
    }
}