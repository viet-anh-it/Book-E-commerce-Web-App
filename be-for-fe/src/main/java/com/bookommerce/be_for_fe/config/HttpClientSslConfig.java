package com.bookommerce.be_for_fe.config;

import java.net.http.HttpClient;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ssl.SslBundle;
import org.springframework.boot.ssl.SslBundles;
import org.springframework.boot.web.client.RestClientCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.JdkClientHttpRequestFactory;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;

// @formatter:off
@Configuration
@FieldDefaults(level = AccessLevel.PRIVATE)
public class HttpClientSslConfig {

    @Value("${server.ssl.bundle}")
    String sslBundleName;
    
    @Bean
    public RestClientCustomizer restClientCustomizer(SslBundles sslBundles) {
        SslBundle sslBundle = sslBundles.getBundle(this.sslBundleName);
        return restClientBuilder -> {
            HttpClient httpClient = HttpClient.newBuilder()
                .sslContext(sslBundle.createSslContext())
                .build();
            restClientBuilder.requestFactory(new JdkClientHttpRequestFactory(httpClient));
        };
    }
}
