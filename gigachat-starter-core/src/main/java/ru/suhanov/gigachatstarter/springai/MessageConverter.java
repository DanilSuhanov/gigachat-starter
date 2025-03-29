package ru.suhanov.gigachatstarter.springai;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.suhanov.dto.ai.gigachat.Message;
import ru.suhanov.gigachatstarter.Utils;

@Service
@RequiredArgsConstructor
@Slf4j
public class MessageConverter {
    public Message covertToGigaFormat(org.springframework.ai.chat.messages.Message springMessage) {
        return Utils.buildMessage(switch (springMessage.getMessageType()) {
            case USER -> Message.RoleEnum.USER;
            case SYSTEM -> Message.RoleEnum.SYSTEM;
            case TOOL -> Message.RoleEnum.FUNCTION;
            case ASSISTANT -> Message.RoleEnum.ASSISTANT;
        }, springMessage.getText());
    }
}
