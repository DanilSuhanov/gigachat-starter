package ru.suhanov.gigachatstarter.generator.fields;

public interface FieldsGenerator {
    String getType();
    String getPattern();
    String generate();
}
