package ru.suhanov.gigachatstarter.generator.fields;

import org.springframework.stereotype.Component;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

@Component
public class RqTmZonedGenerator implements FieldsGenerator {
    @Override
    public String getType() {
        return "rq_tm_zoned";
    }

    @Override
    public String getPattern() {
        return "yyyy-MM-dd'T'HH:mm:ssXXX";
    }

    @Override
    public String generate() {
        return ZonedDateTime.now().format(DateTimeFormatter.ofPattern(getPattern()));
    }
}
