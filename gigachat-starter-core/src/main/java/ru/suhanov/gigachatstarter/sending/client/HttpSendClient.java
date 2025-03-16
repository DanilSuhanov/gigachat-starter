package ru.suhanov.gigachatstarter.sending.client;

import org.springframework.http.HttpMethod;
import ru.suhanov.gigachatstarter.sending.client.model.HttpResult;

import java.net.URI;
import java.util.Map;

public interface HttpSendClient {
    <T, R> HttpResult<R> request(T request, Class<R> resultType, Map<String, String> headers, URI uri, HttpMethod httpMethod);
}
