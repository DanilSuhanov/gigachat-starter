package ru.suhanov.gigachatstarter.sending.client;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import ru.suhanov.gigachatstarter.sending.client.exception.SenderException;
import ru.suhanov.gigachatstarter.sending.client.model.HttpResult;
import ru.suhanov.gigachatstarter.sending.client.prop.SenderProp;

import java.io.IOException;
import java.net.URI;
import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor
public class SendClient implements HttpSendClient {
    protected final OkHttpClient okHttpClient;
    protected final ObjectMapper objectMapper;
    protected final SenderProp prop;

    @Override
    public <T, R> HttpResult<R> request(T request, Class<R> resultType, Map<String, String> headers, URI uri, HttpMethod httpMethod) {
        try {
            String requestBody = requestPrepare(request, headers);
            return requestHttp(requestBody, resultType, headers, uri, httpMethod);
        } catch (Exception e) {
            throw new SenderException(e);
        }
    }

    protected <T> String requestPrepare(T request, Map<String, String> headers) throws JsonProcessingException {
        log.info("Тело запроса:");
        String requestBody = null;
        if (request instanceof String str) {
            requestBody = str;
        } else {
            requestBody = objectMapper.writeValueAsString(request);
        }

        log.info(requestBody);
        log.info("Заголовки запроса - {}", headers);
        return requestBody;
    }

    protected <R> HttpResult<R> requestHttp(String body, Class<R> resultType, Map<String, String> headers, URI uri, HttpMethod httpMethod) {
        // Создаем Request.Builder
        Request.Builder requestBuilder = new Request.Builder()
                .url(uri.toString());

        // Добавляем заголовки
        for (Map.Entry<String, String> entry : headers.entrySet()) {
            requestBuilder.addHeader(entry.getKey(), entry.getValue());
        }

        // Устанавливаем метод и тело запроса
        if (httpMethod == HttpMethod.GET) {
            requestBuilder.get();
        } else {
            RequestBody requestBody = RequestBody.create(body, MediaType.parse("application/x-www-form-urlencoded"));
            requestBuilder.method(httpMethod.name(), requestBody);
        }

        // Выполняем запрос
        try (Response response = okHttpClient.newCall(requestBuilder.build()).execute()) {
            // Проверяем, что ответ не null
            if (response.body() == null) {
                throw new SenderException("Ошибка: тело ответа отсутствует.");
            }

            // Получаем тело ответа
            String responseBody = response.body().string();

            log.info("Тело ответа:");
            log.info(responseBody);
            log.info("Заголовки ответа - {}", response.headers().toMultimap());
            log.info("Статус код - {}", response.code());

            // Проверка статуса ответа
            if (prop.getSenderCheckProp().getCheckStatus() && response.code() != prop.getSenderCheckProp().getSuccessStatus()) {
                throw new SenderException("Ошибка обработки ответа. Неверный статус код.");
            }

            // Десериализация тела ответа
            HttpResult<R> result = new HttpResult<>();
            result.setStatusCode(response.code());
            result.setHeaders(response.headers().toMultimap());

            try {
                result.setBody(resultType.equals(String.class) ? (R) responseBody : objectMapper.readValue(responseBody, resultType));
            } catch (JsonProcessingException e) {
                throw new SenderException("Ошибка при десериализации ответа.", e);
            }

            return result;
        } catch (IOException e) {
            log.error("Ошибка при выполнении запроса: {}", e.toString());
            throw new SenderException("Ошибка при выполнении запроса.", e);
        }
    }
}