package ru.suhanov.gigachatstarter.gigachatapiservice.toolwrapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.stereotype.Service;
import ru.suhanov.dto.ai.gigachat.ChatFunctionsInner;
import ru.suhanov.gigachatstarter.gigachatapiservice.prop.ToolProperty;
import ru.suhanov.gigachatstarter.gigachatapiservice.toolfinder.ToolSpecParser;
import ru.suhanov.gigachatstarter.gigachatapiservice.toolwrapper.toolProvider.AvailableForToolParse;
import ru.suhanov.gigachatstarter.sending.client.SendClient;

import java.net.URI;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ToolWrapperHttp implements ToolWrapper {
    protected final SendClient sendClient;
    protected final ToolProperty toolProperty;

    protected final ToolSpecParser toolSpecParser;
    protected final List<AvailableForToolParse> toolClasses;

    @Override
    public List<ChatFunctionsInner> getTools() {
        List<ChatFunctionsInner> tools = getLocalTools();

        ToolProperty.ToolWrapProp toolWrapProp = toolProperty.getToolWrapProp();
        if (toolWrapProp.getMsType().equals(ToolProperty.ToolWrapProp.MSType.AGENT)) {
            for (String url : toolWrapProp.getToolProvidersUrl()) {
                tools.addAll(sendClient.requestGet(ChatFunctionsInnerList.class, null, URI.create(url)).getBody().getValue());
            }
        }

        return tools;
    }

    protected List<ChatFunctionsInner> getLocalTools() {
        return toolClasses.stream().flatMap(toolClass -> toolSpecParser.getToolSpecs(toolClass.getClass()).stream()).toList();
    }
}
