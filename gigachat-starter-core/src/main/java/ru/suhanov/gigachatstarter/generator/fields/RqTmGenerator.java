package ru.suhanov.gigachatstarter.generator.fields;

import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Component
public class RqTmGenerator implements FieldsGenerator {
    @Override
    public String getType() {
        return "rq_tm";
    }

    @Override
    public String getPattern() {
        return "yyyy-MM-dd'T'HH:mm:ss";
    }

    @Override
    public String generate() {
        return LocalDateTime.now().format(DateTimeFormatter.ofPattern(getPattern()));
    }
}
