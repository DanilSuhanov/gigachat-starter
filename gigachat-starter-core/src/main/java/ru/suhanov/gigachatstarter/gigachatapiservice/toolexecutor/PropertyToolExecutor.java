package ru.suhanov.gigachatstarter.gigachatapiservice.toolexecutor;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import ru.suhanov.dto.ai.gigachat.MessagesResFunctionCall;
import ru.suhanov.gigachatstarter.gigachatapiservice.prop.ToolProperty;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service("PropertyToolExecutor")
@Slf4j
@ConditionalOnProperty(value = "tool.toolParsingMode", havingValue = "PROPERTY")
public class PropertyToolExecutor extends ToolExecutor {
    protected final ToolProperty toolProperty;

    public PropertyToolExecutor(ToolProperty toolProperty) {
        this.toolProperty = toolProperty;
    }

    @Override
    protected Optional<String> getToolNameIfMethodIsTool(Method method) {
        return toolProperty.getToolProps().stream().allMatch(prop -> prop.getMethodName().equals(method.getName()))
                ? Optional.of(method.getName())
                : Optional.empty();
    }

    @Override
    protected String getMethodName(MessagesResFunctionCall functionCall) {
        return toolProperty.getToolProps().stream()
                .filter(prop -> prop.getName().equals(functionCall.getName()))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Не найден toolProp для toolMethodName - " + functionCall.getName()))
                .getMethodName();
    }
}
