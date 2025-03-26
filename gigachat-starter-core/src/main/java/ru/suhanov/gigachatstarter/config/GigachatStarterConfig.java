package ru.suhanov.gigachatstarter.config;


import okhttp3.OkHttpClient;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import ru.suhanov.gigachatstarter.gigachatapimapper.tooldesc.prop.ToolProperty;
import ru.suhanov.gigachatstarter.secretholder.fromproperty.AiProperties;
import ru.suhanov.gigachatstarter.sending.client.prop.SenderProp;

@Configuration
@EnableConfigurationProperties({AiProperties.class, SenderProp.class, ToolProperty.class})
@ComponentScan("ru.suhanov.gigachatstarter")
public class GigachatStarterConfig {

    @Bean
    public OkHttpClient okHttpClient(SenderProp senderProp) throws Exception {
        SenderProp.SenderCertProp certProp = senderProp.getSenderCertProp();
        OkHttpClient.Builder okBuilder = new OkHttpClient.Builder()
                .callTimeout(senderProp.getTimeout())
                .connectTimeout(senderProp.getTimeout())
                .readTimeout(senderProp.getTimeout())
                .writeTimeout(senderProp.getTimeout());

        boolean hasCertFile = certProp.getCertFilePath() != null;
        boolean hasKeyCert = certProp.getKeyFilePath() != null;
        boolean hasCaBundleCert = certProp.getCaBundleFile() != null;

        return null;
    }
}
