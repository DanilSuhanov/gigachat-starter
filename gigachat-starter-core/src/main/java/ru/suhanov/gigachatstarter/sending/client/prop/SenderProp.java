package ru.suhanov.gigachatstarter.sending.client.prop;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;

@Data
@ConfigurationProperties("sender-client")
public class SenderProp {

    private SenderCheckProp senderCheckProp = new SenderCheckProp();
    private SenderCertProp senderCertProp = new SenderCertProp();
    private Duration timeout = Duration.ofMinutes(1);

    @Data
    public static class SenderCheckProp {
        private Boolean checkStatus = true;
        private Integer successStatus = 200;
    }

    @Data
    public static class SenderCertProp {
        private String certFilePath;
        private String keyFilePath;
        private String caBundleFile;
    }
}
