package ru.suhanov.gigachatstarter.gigachatapiservice.toolwrapper;

import ru.suhanov.dto.ai.gigachat.ChatFunctionsInner;

import java.util.List;

public interface ToolWrapper {
    List<ChatFunctionsInner> getTools();
}
