package ru.suhanov.gigachatstarter.gigachatapiservice.prop;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
@ConfigurationProperties("tool")
public class ToolProperty {
    private ToolParsingMode toolParsingMode;

    private ToolWrapProp toolWrapProp = new ToolWrapProp();
    private List<ToolProp> toolProps = new ArrayList<>();
    private Map<String, String> returnObjectFieldDescMap = new HashMap<>();

    @Data
    public static class ToolProp {
        private String name;
        private String methodName;
        private String description;
        private ToolSource source = ToolSource.LOCAL;

        private List<ParameterProp> parametersProps;

        @Data
        public static class ParameterProp {
            private String name;
            private String description;
            private boolean required = true;
            private List<String> allowValues;
        }

        public enum ToolSource {
            LOCAL,
            REMOTE
        }
    }

    @Data
    public static class ToolWrapProp {
        private MSType msType;
        private List<String> toolProvidersUrl = new ArrayList<>();

        public enum MSType {
            AGENT,
            TOOL_PROVIDER
        }
    }

    public enum ToolParsingMode {
        PROPERTY,
        ANNOTATION
    }
}
