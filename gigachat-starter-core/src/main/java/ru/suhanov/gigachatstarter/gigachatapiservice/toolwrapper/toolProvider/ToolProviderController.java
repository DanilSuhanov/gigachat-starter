package ru.suhanov.gigachatstarter.gigachatapiservice.toolwrapper.toolProvider;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.suhanov.gigachatstarter.gigachatapiservice.toolwrapper.ChatFunctionsInnerList;
import ru.suhanov.gigachatstarter.gigachatapiservice.toolwrapper.ToolWrapper;

@RestController
@RequiredArgsConstructor
@Slf4j
public class ToolProviderController {
    protected final ToolWrapper toolWrapper;

    @GetMapping("/getTools")
    public ResponseEntity<ChatFunctionsInnerList> getTools() {
        log.info("Пришёл запрос на получение тулов");
        return ResponseEntity.ok(new ChatFunctionsInnerList(toolWrapper.getTools()));
    }
}
