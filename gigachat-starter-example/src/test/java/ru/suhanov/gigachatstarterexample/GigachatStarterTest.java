package ru.suhanov.gigachatstarterexample;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import ru.suhanov.dto.ai.gigachat.*;
import ru.suhanov.gigachatstarter.config.GigachatStarterConfig;
import ru.suhanov.gigachatstarter.gigachatapiservice.history.History;
import ru.suhanov.gigachatstarter.gigachatapiservice.toolexecutor.ToolExecutor;
import ru.suhanov.gigachatstarter.service.GigachatModelLocalImpl;
import ru.suhanov.gigachatstarter.springai.GigachatModel;

import java.util.List;

@SpringBootTest
@Slf4j
@Import(GigachatStarterConfig.class)
public class GigachatStarterTest {
    @Autowired
    GigachatModelLocalImpl gigachatModel;
    @Autowired
    ToolExecutor toolExecutor;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    GigachatModel springGigachatModel;

//    @Test
//    @SneakyThrows
//    void localApiTest() {
//        History history = new History("Ты почтальон.")
//                .addUserMessage("Выведи информацию по посылке 1234");
//
//        List<ChatFunctionsInner> chatFunctionsInners = toolWrapperHttp.getTools();
//        log.info(objectMapper.writeValueAsString(chatFunctionsInners));
//
//        Chat chat = new Chat("GigaChat", history.getMessages()).functions(chatFunctionsInners);
//        ChatCompletion result = gigachatModel.prompt(chat);
//        log.info(result.toString());
//
//        MessagesRes messagesRes = result.getChoices().get(0).getMessage();
//        history.addAiMessage(messagesRes, result.getChoices().get(0).getFinishReason(), objectMapper);
//
//        toolExecutor.registerToolClass(PostaService.class);
//        Object sendRs = toolExecutor.execute(messagesRes.getFunctionCall());
//        log.info(sendRs.toString());
//
//        history.addToolResult(sendRs, objectMapper);
//
//        chat = new Chat("GigaChat", history.getMessages()).functions(chatFunctionsInners);
//        result = gigachatModel.prompt(chat);
//        log.info(result.toString());
//    }

    @Test
    void springAiTest() {
        Prompt prompt = new Prompt(new SystemMessage("Ты почтальон."), new UserMessage("Выведи информацию по посылке 1234"));
        String response = ChatClient.create(springGigachatModel)
                .prompt(prompt)
                .tools(new PostaService())
                .call()
                .content();
        log.info(response);
    }
}
