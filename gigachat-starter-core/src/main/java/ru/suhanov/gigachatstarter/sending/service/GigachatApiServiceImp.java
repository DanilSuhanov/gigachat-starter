package ru.suhanov.gigachatstarter.sending.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import ru.suhanov.dto.ai.gigachat.Chat;
import ru.suhanov.dto.ai.gigachat.ChatCompletion;
import ru.suhanov.dto.ai.gigachat.Token;
import ru.suhanov.gigachatstarter.generator.RqUidGenerator;
import ru.suhanov.gigachatstarter.sending.client.SendClient;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor
@ConditionalOnProperty(name = "tool.toolWrapProp.msType", havingValue = "AGENT")
public class GigachatApiServiceImp implements GigachatApiService {
    protected final RqUidGenerator rqUidGenerator;
    protected final SendClient sendClient;
    protected final SendClient sendClientForTokenGet;


    @Override
    public Token getToken(String scope, String authorisationKey) {
        Map<String, String> headers = new HashMap<>();
        headers.put("RqUID", rqUidGenerator.generate());
        headers.put("Authorization", "Basic " + authorisationKey);
        headers.put("Content-Type", "application/x-www-form-urlencoded");
        headers.put("Accept", "application/json");

        return sendClientForTokenGet.request(scope, Token.class, headers, URI.create("https://ngw.devices.sberbank.ru:9443/api/v2/oauth"), HttpMethod.POST).getBody();
    }

    @Override
    public ChatCompletion request(Chat chat, String token) {
        Map<String, String> headers = new HashMap<>();
        headers.put("Authorization", "Bearer " + token);
        headers.put("Content-Type", "application/x-www-form-urlencoded");
        headers.put("Accept", "application/json");

        return sendClient.request(chat, ChatCompletion.class, headers, URI.create("https://gigachat.devices.sberbank.ru/api/v1/chat/completions"), HttpMethod.POST).getBody();
    }
}
