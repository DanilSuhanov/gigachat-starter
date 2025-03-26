package ru.suhanov.gigachatstarter.secretholder.fromproperty;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.suhanov.dto.ai.gigachat.Token;
import ru.suhanov.gigachatstarter.secretholder.SecretHolder;
import ru.suhanov.gigachatstarter.sending.service.GigachatApiService;

import java.time.Instant;

@Service
@Slf4j
public class SecretHolderFromProperty implements SecretHolder {
    protected final GigachatApiService apiService;
    protected final AiProperties aiProperties;
    protected Token token;

    public SecretHolderFromProperty(GigachatApiService apiService, AiProperties aiProperties) {
        this.apiService = apiService;
        this.aiProperties = aiProperties;
        if (aiProperties.getToken() != null) {
            token = new Token().accessToken(aiProperties.getToken()).expiresAt(Instant.now().plusSeconds(60 * 30).getEpochSecond());
        } else {
            updateToken();
        }
    }

    protected void updateToken() {
        token = apiService.getToken("scope=GIGACHAT_API_PERS", aiProperties.getAuthorizationKey());
    }

    @Override
    public String getToken() {
        Instant instant = Instant.ofEpochMilli(token.getExpiresAt());
        if (instant.isBefore(Instant.now()))
            updateToken();
        return token.getAccessToken();
    }

    @Override
    public String getClientId() {
        return aiProperties.getClientId();
    }

    @Override
    public String getClientSecret() {
        return aiProperties.getClientId();
    }
}
