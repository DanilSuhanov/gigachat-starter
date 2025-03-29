package ru.suhanov.gigachatstarter.springai;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.model.Generation;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.stereotype.Service;
import ru.suhanov.dto.ai.gigachat.*;
import ru.suhanov.gigachatstarter.gigachatapiservice.history.History;
import ru.suhanov.gigachatstarter.gigachatapiservice.toolexecutor.ToolExecutor;
import ru.suhanov.gigachatstarter.gigachatapiservice.toolwrapper.ToolWrapper;
import ru.suhanov.gigachatstarter.service.GigachatModelLocalImpl;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@ConditionalOnBean(GigachatModelLocalImpl.class)
public class GigachatModel implements ChatModel {
    protected final GigachatModelLocalImpl gigachatModel;
    protected final MessageConverter messageConverter;
    protected final ToolWrapper toolWrapper;
    protected final ToolExecutor toolExecutor;
    protected final ObjectMapper objectMapper;

    @Override
    public ChatResponse call(Prompt prompt) {
        try {
            History history = new History(prompt.getInstructions().stream()
                    .map(messageConverter::covertToGigaFormat).collect(Collectors.toList()));

            String chatModel = prompt.getOptions() != null ? prompt.getOptions().getModel() : null;
            if (chatModel == null)
                chatModel = "GigaChat";
            Chat chat = new Chat(chatModel, history.getMessages()).functions(toolWrapper.getTools());
            ChatCompletion response = gigachatModel.prompt(chat);
            Choices choices = response.getChoices().get(0);
            history.addAiMessage(choices.getMessage(), choices.getFinishReason(), objectMapper);
            if (history.getLastFinishReason().equals(Choices.FinishReasonEnum.FUNCTION_CALL)) {
                Object toolResult = toolExecutor.execute(choices.getMessage().getFunctionCall());
                history.addToolResult(toolResult, objectMapper);
                chat.setMessages(history.getMessages());
                response = gigachatModel.prompt(chat);
                choices = response.getChoices().get(0);
                history.addAiMessage(choices.getMessage(), choices.getFinishReason(), objectMapper);
            }

            List<Message> messages = history.getMessages();
            Generation generation = new Generation(new AssistantMessage(messages.get(messages.size() - 1).getContent()));

            return ChatResponse.builder()
                    .generations(List.of(generation))
                    .metadata("hist", history)
                    .build();
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    protected ChatResponse buildChatResponse(History history) {
        List<Message> messages = history.getMessages();
        Generation generation = new Generation(new AssistantMessage(messages.get(messages.size() - 1).getContent()));

        return ChatResponse.builder()
                .generations(List.of(generation))
                .metadata("hist", history)
                .build();
    }
}
