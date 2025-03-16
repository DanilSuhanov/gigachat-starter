package ru.suhanov.gigachatstarter.gigachatapimapper.tooldesc.prop;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
@ConfigurationProperties("tool")
public class ToolProperty {
    private List<ToolProp> toolProps = new ArrayList<>();
    private Map<String, String> returnObjectFieldDescMap = new HashMap<>();

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
