package com.bookommerce.be_for_fe.config;

import jakarta.annotation.PostConstruct;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import java.io.IOException;

@Configuration
public class SslConfig {

    @PostConstruct
    public void configureSSL() throws IOException {
        ClassPathResource resource = new ClassPathResource("bookommerce-ssl.p12");
        String path = resource.getFile().getAbsolutePath();
        System.setProperty("javax.net.ssl.trustStore", path);
        System.setProperty("javax.net.ssl.trustStorePassword", "123456");
        System.setProperty("javax.net.ssl.trustStoreType", "PKCS12");
    }
}