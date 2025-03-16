package ru.suhanov.gigachatstarter.gigachatapimapper.tooldesc.toolfinder;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.suhanov.dto.ai.gigachat.ChatFunctionsInner;
import ru.suhanov.gigachatstarter.gigachatapimapper.tooldesc.annotation.Description;
import ru.suhanov.gigachatstarter.gigachatapimapper.tooldesc.annotation.Required;
import ru.suhanov.gigachatstarter.gigachatapimapper.tooldesc.prop.ToolProperty;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service("PropertyToolSpecFinder")
@Slf4j
public class PropertyToolSpecFinder extends ToolSpecFinder {
    private final ToolProperty toolProperty;
    private final Map<String, ToolProperty.ToolProp> toolProps;

    public PropertyToolSpecFinder(ToolProperty property) {
        this.toolProperty = property;
        this.toolProps = property.getToolProps().stream().collect(Collectors.toMap(ToolProperty.ToolProp::getName, Function.identity()));
    }

    @Override
    protected Optional<ChatFunctionsInner> handleMethod(Method method) {
        if (toolProps.containsKey(method.getName())) {
            ToolProperty.ToolProp toolProp = toolProps.get(method.getName());

            ChatFunctionsInner toolDescription = new ChatFunctionsInner();
            toolDescription.setName(method.getName());
            toolDescription.setDescription(toolProp.getDescription());

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

    @Override
    protected void hadnleParameter(Parameter param, Map<String, Object> paramSchema, List<String> required, String methodName) {
        ToolProperty.ToolProp.ParameterProp paramProp = toolProps.get(methodName).getParametersProps().stream()
                .filter(parameterProp -> parameterProp.getName().equals(param.getName())).findFirst()
                .orElse(null);
        if (paramProp == null)
            return;

        if (paramProp.getDescription() != null) {
            paramSchema.put("description", paramProp.getDescription());
        }
        if (paramProp.getAllowValues() != null && !paramProp.getAllowValues().isEmpty()) {
            paramSchema.put("enum", String.join(", ", paramProp.getAllowValues()));
        }
        if (paramProp.isRequired()) {
            required.add(methodName);
        }
    }

    @Override
    protected void handleReturnField(Field field, Map<String, Object> fieldSchema, List<String> required, String fieldName) {
        if (field.isAnnotationPresent(Description.class)) {
            fieldSchema.put("description", toolProperty.getReturnObjectFieldDescMap().get(field.getAnnotation(Description.class).value()));
        }
        if (field.isAnnotationPresent(Required.class)) {
            required.add(fieldName);
        }
    }
}
