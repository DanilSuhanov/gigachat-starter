package ru.suhanov.gigachatstarter.sending.client.prop;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties("sender-client")
public class SenderProp {

    private SenderCheckProp senderCheckProp = new SenderCheckProp();

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SenderCheckProp {
        private Boolean checkStatus = true;
        public Integer successStatus = 200;
    }
}
