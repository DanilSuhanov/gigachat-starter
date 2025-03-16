package ru.suhanov.gigachatstarter.generator.tooldesc;

import org.springframework.stereotype.Service;
import ru.suhanov.dto.ai.gigachat.ChatFunctionsInner;
import ru.suhanov.gigachatstarter.generator.tooldesc.annotation.*;

import java.lang.reflect.*;
import java.util.*;

@Service
public class ToolFinder {

    /**
     * Находит все методы, помеченные аннотацией @Tool, и возвращает их описание.
     *
     * @param clazz Класс, в котором нужно искать методы.
     * @return Список описаний методов.
     */
    public List<ChatFunctionsInner> findTools(Class<?> clazz) {
        List<ChatFunctionsInner> tools = new ArrayList<>();

        for (Method method : clazz.getDeclaredMethods()) {
            handleMethod(method).ifPresent(tools::add);
        }

        return tools;
    }

    protected Optional<ChatFunctionsInner> handleMethod(Method method) {
        if (method.isAnnotationPresent(Tool.class)) {
            Tool toolAnnotation = method.getAnnotation(Tool.class);

            ChatFunctionsInner toolDescription = new ChatFunctionsInner();
            toolDescription.setName(toolAnnotation.name());
            toolDescription.setDescription(toolAnnotation.description());

            // Генерация схемы для параметров
            Map<String, Object> parametersSchema = generateSchemaForParameters(method);
            toolDescription.setParameters(parametersSchema);

            // Генерация схемы для возвращаемых значений
            Map<String, Object> returnParametersSchema = generateSchemaForReturnType(method);
            toolDescription.setReturnParameters(returnParametersSchema);

            return Optional.of(toolDescription);
        }
        return Optional.empty();
    }

    /**
     * Генерирует схему для параметров метода.
     */
    private Map<String, Object> generateSchemaForParameters(Method method) {
        Map<String, Object> schema = new HashMap<>();
        schema.put("type", "object");

        Map<String, Object> properties = new HashMap<>();
        List<String> required = new ArrayList<>();

        for (Parameter param : method.getParameters()) {
            String paramName = param.getName();
            Class<?> paramType = param.getType();

            Map<String, Object> paramSchema = generateSchemaForType(paramType);

            // Добавляем аннотации параметра
            if (param.isAnnotationPresent(Description.class)) {
                paramSchema.put("description", param.getAnnotation(Description.class).value());
            }
            if (param.isAnnotationPresent(AllowedValues.class)) {
                paramSchema.put("enum", Arrays.asList(param.getAnnotation(AllowedValues.class).value()));
            }

            properties.put(paramName, paramSchema);

            // Если параметр обязательный, добавляем его в список required
            if (!param.isAnnotationPresent(NotRequired.class)) {
                required.add(paramName);
            }
        }

        schema.put("properties", properties);
        if (!required.isEmpty()) {
            schema.put("required", required);
        }

        return schema;
    }

    /**
     * Генерирует схему для возвращаемого типа.
     */
    private Map<String, Object> generateSchemaForReturnType(Method method) {
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
    private Map<String, Object> generateSchemaForType(Class<?> type) {
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

                // Добавляем аннотации поля
                if (field.isAnnotationPresent(Description.class)) {
                    fieldSchema.put("description", field.getAnnotation(Description.class).value());
                }

                properties.put(fieldName, fieldSchema);

                // Если поле обязательное, добавляем его в список required
                if (!field.isAnnotationPresent(NotRequired.class)) {
                    required.add(fieldName);
                }
            }

            schema.put("properties", properties);
            if (!required.isEmpty()) {
                schema.put("required", required);
            }
        }

        return schema;
    }

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