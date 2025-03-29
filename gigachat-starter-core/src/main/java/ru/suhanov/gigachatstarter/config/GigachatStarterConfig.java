package ru.suhanov.gigachatstarter.config;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import nl.altindag.ssl.SSLFactory;
import nl.altindag.ssl.pem.util.PemUtils;
import okhttp3.OkHttpClient;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import ru.suhanov.gigachatstarter.gigachatapiservice.prop.ToolProperty;
import ru.suhanov.gigachatstarter.secretholder.fromproperty.AiProperties;
import ru.suhanov.gigachatstarter.sending.client.SendClient;
import ru.suhanov.gigachatstarter.sending.prop.SenderProp;

import javax.net.ssl.X509ExtendedKeyManager;
import javax.net.ssl.X509ExtendedTrustManager;
import java.nio.file.Path;
import java.nio.file.Paths;

@Configuration
@EnableConfigurationProperties({AiProperties.class, SenderProp.class, ToolProperty.class})
@AutoConfiguration
@ComponentScan("ru.suhanov.gigachatstarter")
public class GigachatStarterConfig {

    @Bean("fullCertClient")
    public OkHttpClient fullCertClient(SenderProp senderProp) {
        SenderProp.SenderCertProp certProp = senderProp.getSenderCertProp();
        OkHttpClient.Builder okBuilder = new OkHttpClient.Builder()
                .callTimeout(senderProp.getTimeout())
                .connectTimeout(senderProp.getTimeout())
                .readTimeout(senderProp.getTimeout())
                .writeTimeout(senderProp.getTimeout());

        boolean hasCertFile = certProp.getCertFilePath() != null;
        boolean hasKeyCert = certProp.getKeyFilePath() != null;
        boolean hasCaBundleCert = certProp.getCaBundleFile() != null;

        if (hasCertFile && hasKeyCert && hasCaBundleCert) {
            X509ExtendedKeyManager keyManager = PemUtils.loadIdentityMaterial(Paths.get(certProp.getCertFilePath()), Paths.get(certProp.getKeyFilePath()));
            X509ExtendedTrustManager trustManager = PemUtils.loadTrustMaterial(Path.of(certProp.getCaBundleFile()));
            SSLFactory sslFactory = SSLFactory.builder().withIdentityMaterial(keyManager).withTrustMaterial(trustManager).build();
            okBuilder.sslSocketFactory(sslFactory.getSslSocketFactory(), sslFactory.getTrustManager().get());
        } else if (hasCaBundleCert) {
            X509ExtendedTrustManager trustManager = PemUtils.loadTrustMaterial(Path.of(certProp.getCaBundleFile()));
            SSLFactory sslFactory = SSLFactory.builder().withTrustMaterial(trustManager).build();
            okBuilder.sslSocketFactory(sslFactory.getSslSocketFactory(), sslFactory.getTrustManager().get());
        }

        return okBuilder.build();
    }

    @Bean("certClient")
    public OkHttpClient certClient(SenderProp prop) {
        SenderProp.SenderCertProp certProp = prop.getSenderCertProp();
        OkHttpClient.Builder okBuilder = new OkHttpClient.Builder()
                .callTimeout(prop.getTimeout())
                .connectTimeout(prop.getTimeout())
                .readTimeout(prop.getTimeout())
                .writeTimeout(prop.getTimeout());

        if (certProp.getCaBundleFile() != null) {
            X509ExtendedTrustManager trustManager = PemUtils.loadTrustMaterial(Path.of(certProp.getCaBundleFile()));
            SSLFactory sslFactory = SSLFactory.builder().withTrustMaterial(trustManager).build();
            okBuilder.sslSocketFactory(sslFactory.getSslSocketFactory(), sslFactory.getTrustManager().get());
        }

        return okBuilder.build();
    }

    @Bean("sendClient")
    @ConditionalOnBean(name = {"fullCertClient"})
    public SendClient sendClient(@Qualifier("fullCertClient") OkHttpClient okHttpClient, ObjectMapper objectMapper, SenderProp prop) {
        return new SendClient(okHttpClient, objectMapper, prop);
    }

    @Bean("sendClientForTokenGet")
    @ConditionalOnBean(name = {"certClient"})
    public SendClient sendClientForTokenGet(@Qualifier("certClient") OkHttpClient okHttpClient, ObjectMapper objectMapper, SenderProp prop) {
        return new SendClient(okHttpClient, objectMapper, prop);
    }

    @Bean
    public ObjectMapper objectMapper() {
        return new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT);
    }
}
