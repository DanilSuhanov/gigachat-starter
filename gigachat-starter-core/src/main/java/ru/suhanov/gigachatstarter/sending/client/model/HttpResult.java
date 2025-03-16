package ru.suhanov.gigachatstarter.sending.client.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class HttpResult<Body> {
    private Body body;
    private Map<String, List<String>> headers;
    private Integer statusCode;
}
