package ru.suhanov.gigachatstarter.sending.service;

import ru.suhanov.dto.ai.gigachat.Chat;
import ru.suhanov.dto.ai.gigachat.ChatCompletion;
import ru.suhanov.dto.ai.gigachat.Token;

public interface GigachatApiService {
    Token getToken(String scope, String authorisationKey);
    ChatCompletion request(Chat chat, String token);
}
