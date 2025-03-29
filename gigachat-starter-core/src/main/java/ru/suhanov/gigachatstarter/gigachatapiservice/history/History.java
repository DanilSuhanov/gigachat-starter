package ru.suhanov.gigachatstarter.gigachatapiservice.history;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.NoArgsConstructor;
import ru.suhanov.dto.ai.gigachat.Choices;
import ru.suhanov.dto.ai.gigachat.Message;
import ru.suhanov.dto.ai.gigachat.MessagesRes;
import ru.suhanov.gigachatstarter.Utils;

import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
@Getter
public class History {
    private List<Message> messages = new ArrayList<>();
    private Choices.FinishReasonEnum lastFinishReason = Choices.FinishReasonEnum.STOP;

    public History(String systemMessage) {
        this.messages = new ArrayList<>(List.of(Utils.buildMessage(Message.RoleEnum.SYSTEM, systemMessage)));
    }

    public History(List<Message> messages) {
        this.messages = messages;
    }

    public History addSystemMessage(String systemMessage) {
        Message message = Utils.buildMessage(Message.RoleEnum.SYSTEM, systemMessage);

        if (messages.isEmpty())
            messages.add(message);
        else {
            List<Message> temp = new ArrayList<>(List.of(message));
            temp.addAll(messages);
            messages = temp;
        }
        return this;
    }

    public History addUserMessage(String userMessage) {
        Message message = Utils.buildMessage(Message.RoleEnum.USER, userMessage);
        messages.add(message);
        return this;
    }

    public History addAiMessage(MessagesRes messagesRes, Choices.FinishReasonEnum finishReasonEnum, ObjectMapper objectMapper) throws JsonProcessingException {
        lastFinishReason = finishReasonEnum;
        Message aiMessage;
        if (finishReasonEnum.equals(Choices.FinishReasonEnum.FUNCTION_CALL)) {
            aiMessage = Utils.buildMessage(
                    Message.RoleEnum.ASSISTANT,
                    objectMapper.writeValueAsString(messagesRes.getFunctionCall())
            );
            aiMessage.setFunctionsStateId(messagesRes.getFunctionsStateId());
        } else {
            aiMessage = Utils.buildMessage(Message.RoleEnum.ASSISTANT, messagesRes.getContent());
        }
        messages.add(aiMessage);
        return this;
    }

    public History addToolResult(Object toolResult, ObjectMapper objectMapper) throws JsonProcessingException {
        if (!lastFinishReason.equals(Choices.FinishReasonEnum.FUNCTION_CALL))
            throw new IllegalArgumentException("Последнее состояние истории не является вызовом тула.");
        Message toolAnswer = Utils.buildMessage(Message.RoleEnum.FUNCTION, objectMapper.writeValueAsString(toolResult));
        messages.add(toolAnswer);
        return this;
    }
}
