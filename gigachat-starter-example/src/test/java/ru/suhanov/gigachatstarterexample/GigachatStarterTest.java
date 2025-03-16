package ru.suhanov.gigachatstarterexample;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import ru.suhanov.dto.ai.gigachat.Chat;
import ru.suhanov.dto.ai.gigachat.ChatCompletion;
import ru.suhanov.dto.ai.gigachat.ChatFunctionsInner;
import ru.suhanov.dto.ai.gigachat.Message;
import ru.suhanov.gigachatstarter.config.GigachatStarterConfig;
import ru.suhanov.gigachatstarter.generator.tooldesc.ToolFinder;
import ru.suhanov.gigachatstarter.generator.tooldesc.ToolRequestMapper;
import ru.suhanov.gigachatstarter.service.GigachatModelImpl;

import java.util.List;
import java.util.Map;

@SpringBootTest
@Slf4j
@Import(GigachatStarterConfig.class)
public class GigachatStarterTest {
    @Autowired
    GigachatModelImpl gigachatModel;
    @Autowired
    ToolFinder toolFinder;
    @Autowired
    PostaService postaService;

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
        userMessage.setContent("Выведи информаци по посылке 1234. Информация а - АТЕСТ, информация б - БТЕСТ.");

        List<ChatFunctionsInner> chatFunctionsInners = toolFinder.findTools(PostaService.class);
        log.info(mapper.writeValueAsString(chatFunctionsInners));

        Chat chat = new Chat("GigaChat", List.of(systemMessage, userMessage))
                .functions(chatFunctionsInners);

        ChatCompletion result = gigachatModel.prompt(chat);
        SendInfoRq sendInfoRq = ToolRequestMapper.mapToObject((Map<String, Object>) ((Map<String, Object>) result.getChoices().get(0).getMessage().getFunctionCall().getArguments()).get("sendInfoRq"), SendInfoRq.class);
        postaService.getSendInfo(sendInfoRq);
        log.info(result.toString());
    }
}
