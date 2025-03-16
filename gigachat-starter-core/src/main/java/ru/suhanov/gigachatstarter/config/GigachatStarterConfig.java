package ru.suhanov.gigachatstarter.config;


import okhttp3.OkHttpClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import ru.suhanov.gigachatstarter.secretholder.fromproperty.AiProperties;
import ru.suhanov.gigachatstarter.sending.client.prop.SenderProp;
import org.springframework.core.io.Resource;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;

@Configuration
@EnableConfigurationProperties({AiProperties.class, SenderProp.class})
@ComponentScan("ru.suhanov.gigachatstarter")
public class GigachatStarterConfig {
    @Value("${webclient.ssl.ca-bundle-file}") // Путь к CA-сертификату из конфигурации
    private Resource caBundleFile;

    @Bean
    public OkHttpClient okHttpClient() throws Exception {
        // Загрузка CA-сертификата
        CertificateFactory certificateFactory = CertificateFactory.getInstance("X.509");
        Certificate caCertificate;
        try (InputStream caBundleStream = caBundleFile.getInputStream()) {
            caCertificate = certificateFactory.generateCertificate(caBundleStream);
        }

        // Создание KeyStore и добавление CA-сертификата
        KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
        keyStore.load(null, null); // Инициализация пустого KeyStore
        keyStore.setCertificateEntry("caCert", caCertificate);

        // Создание TrustManagerFactory
        TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
        trustManagerFactory.init(keyStore);

        // Создание SSLContext
        SSLContext sslContext = SSLContext.getInstance("TLS");
        sslContext.init(null, trustManagerFactory.getTrustManagers(), null);

        // Создание OkHttpClient с настроенным SSLContext
        return new OkHttpClient.Builder()
                .sslSocketFactory(sslContext.getSocketFactory(), (X509TrustManager) trustManagerFactory.getTrustManagers()[0])
                .build();
    }
}
