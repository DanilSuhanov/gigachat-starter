package ru.suhanov.gigachatstarterexample;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import ru.suhanov.dto.ai.gigachat.Chat;
import ru.suhanov.dto.ai.gigachat.ChatCompletion;
import ru.suhanov.dto.ai.gigachat.ChatFunctionsInner;
import ru.suhanov.dto.ai.gigachat.Message;
import ru.suhanov.gigachatstarter.config.GigachatStarterConfig;
import ru.suhanov.gigachatstarter.gigachatapiservice.toolfinder.ToolSpecFinder;
import ru.suhanov.gigachatstarter.gigachatapiservice.toolexecutor.ToolExecutor;
import ru.suhanov.gigachatstarter.service.GigachatModelImpl;

import java.util.List;

@SpringBootTest
@Slf4j
@Import(GigachatStarterConfig.class)
public class GigachatStarterTest {
    @Autowired
    GigachatModelImpl gigachatModel;
    @Autowired
    @Qualifier("PropertyToolSpecFinder")
    ToolSpecFinder toolSpecFinder;
    @Autowired
    @Qualifier("PropertyToolExecutor")
    ToolExecutor toolExecutor;

    @Test
    @SneakyThrows
    void test() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.enable(SerializationFeature.INDENT_OUTPUT);

        Message systemMessage = new Message();
        systemMessage.setRole(Message.RoleEnum.SYSTEM);
        systemMessage.setContent("Ты почтальон.");

        Message userMessage = new Message();
        userMessage.setRole(Message.RoleEnum.USER);
        userMessage.setContent("Выведи информаци по посылке 1234.");

        List<ChatFunctionsInner> chatFunctionsInners = toolSpecFinder.getToolSpecs(PostaService.class);
        log.info(mapper.writeValueAsString(chatFunctionsInners));

        Chat chat = new Chat("GigaChat", List.of(systemMessage, userMessage))
                .functions(chatFunctionsInners);

        ChatCompletion result = gigachatModel.prompt(chat);
        log.info(result.toString());

        toolExecutor.registerToolClass(PostaService.class);
        Object sendInfoRq = toolExecutor.execute(result.getChoices().get(0).getMessage().getFunctionCall());
        log.info(sendInfoRq.toString());

    }
}
