package ru.suhanov.gigachatstarter.gigachatapiservice.toolwrapper;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.suhanov.dto.ai.gigachat.ChatFunctionsInner;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChatFunctionsInnerList {
    private List<ChatFunctionsInner> value;
}
