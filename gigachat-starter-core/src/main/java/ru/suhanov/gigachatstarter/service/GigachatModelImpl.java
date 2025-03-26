package ru.suhanov.gigachatstarter.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.suhanov.dto.ai.gigachat.Chat;
import ru.suhanov.dto.ai.gigachat.ChatCompletion;
import ru.suhanov.gigachatstarter.secretholder.SecretHolder;
import ru.suhanov.gigachatstarter.sending.service.GigachatApiService;

@Slf4j
@RequiredArgsConstructor
@Service
public class GigachatModelImpl {
    protected final GigachatApiService apiService;
    protected final SecretHolder secretHolder;

    public ChatCompletion prompt(Chat chat) {
        return apiService.request(chat, secretHolder.getToken());
    }
}
