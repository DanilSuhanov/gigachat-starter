package ru.suhanov.gigachatstarter.secretholder.fromproperty;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties("ai")
public class AiProperties {
    private String authorizationKey;
    private String secretKey;
    private String clientId;

    private String token;
}
