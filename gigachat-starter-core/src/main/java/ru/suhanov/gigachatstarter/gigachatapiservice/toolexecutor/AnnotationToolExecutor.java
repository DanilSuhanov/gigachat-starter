package ru.suhanov.gigachatstarter.gigachatapiservice.toolexecutor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import ru.suhanov.dto.ai.gigachat.MessagesResFunctionCall;
import ru.suhanov.gigachatstarter.gigachatapiservice.annotation.Tool;
import ru.suhanov.gigachatstarter.gigachatapiservice.toolwrapper.toolProvider.AvailableForToolParse;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Optional;

@Service("AnnotationToolExecutor")
@Slf4j
@ConditionalOnProperty(value = "tool.toolParsingMode", havingValue = "ANNOTATION")
public class AnnotationToolExecutor extends ToolExecutor {

    public AnnotationToolExecutor(List<AvailableForToolParse> toolClasses) {
        super(toolClasses);
    }

    @Override
    protected Optional<String> getToolNameIfMethodIsTool(Method method) {
        return method.isAnnotationPresent(Tool.class)
                ? Optional.of(method.getAnnotation(Tool.class).name())
                : Optional.empty();
    }

    @Override
    protected String getMethodName(MessagesResFunctionCall functionCall) {
        return functionCall.getName();
    }
}
