package ru.suhanov.gigachatstarter.generator;

public interface FieldsGenerator {
    String getType();
    String getPattern();
    String generate();
}
