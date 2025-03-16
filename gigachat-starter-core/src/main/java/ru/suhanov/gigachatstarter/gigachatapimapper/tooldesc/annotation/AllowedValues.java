package ru.suhanov.gigachatstarter.gigachatapimapper.tooldesc.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface AllowedValues {
    String[] value();
}
