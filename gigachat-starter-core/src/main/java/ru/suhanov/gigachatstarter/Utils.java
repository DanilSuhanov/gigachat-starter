package ru.suhanov.gigachatstarter;

import lombok.experimental.UtilityClass;
import ru.suhanov.dto.ai.gigachat.Message;

@UtilityClass
public class Utils {
    public Message buildMessage(Message.RoleEnum role, String content) {
        Message message = new Message();
        message.setRole(role);
        message.setContent(content);
        return message;
    }
}
