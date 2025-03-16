package ru.suhanov.gigachatstarter.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.ArrayList;
import java.util.List;

@Data
@ConfigurationProperties
public class ToolProperty {
    private List<ToolProp> toolProps = new ArrayList<>();

    @Data
    public static class ToolProp {
        private String name;
        private String description;

        private List<ParameterProp> parametersProps;

        @Data
        public static class ParameterProp {
            private String name;
            private String description;
            private boolean required = true;
            private List<String> allowValues;
        }
    }
}
