package ru.suhanov.gigachatstarter.gigachatapimapper.tooldesc.toolfinder;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.suhanov.dto.ai.gigachat.ChatFunctionsInner;
import ru.suhanov.gigachatstarter.gigachatapimapper.tooldesc.annotation.AllowedValues;
import ru.suhanov.gigachatstarter.gigachatapimapper.tooldesc.annotation.Description;
import ru.suhanov.gigachatstarter.gigachatapimapper.tooldesc.annotation.Required;
import ru.suhanov.gigachatstarter.gigachatapimapper.tooldesc.annotation.Tool;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service("AnnotationToolSpecFinder")
@RequiredArgsConstructor
@Slf4j
public class AnnotationToolSpecFinder extends ToolSpecFinder {

    @Override
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

    @Override
    protected void hadnleParameter(Parameter param, Map<String, Object> paramSchema, List<String> required, String methodName) {
        // Добавляем аннотации параметра
        if (param.isAnnotationPresent(Description.class)) {
            paramSchema.put("description", param.getAnnotation(Description.class).value());
        }
        if (param.isAnnotationPresent(AllowedValues.class)) {
            paramSchema.put("enum", Arrays.asList(param.getAnnotation(AllowedValues.class).value()));
        }
        if (param.isAnnotationPresent(Required.class)) {
            required.add(param.getName());
        }
    }

    @Override
    protected void handleReturnField(Field field, Map<String, Object> fieldSchema, List<String> required, String fieldName) {
        // Добавляем аннотации поля
        if (field.isAnnotationPresent(Description.class)) {
            fieldSchema.put("description", field.getAnnotation(Description.class).value());
        }
        if (field.isAnnotationPresent(Required.class)) {
            required.add(fieldName);
        }
    }
}
