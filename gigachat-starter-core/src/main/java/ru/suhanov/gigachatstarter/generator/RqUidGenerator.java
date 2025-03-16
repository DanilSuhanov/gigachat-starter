package ru.suhanov.gigachatstarter.generator;

import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class RqUidGenerator implements FieldsGenerator {
    @Override
    public String getType() {
        return "rq_uid";
    }

    @Override
    public String getPattern() {
        return "(([0-9a-fA-F-])36)";
    }

    @Override
    public String generate() {
        return UUID.randomUUID().toString();
    }
}
