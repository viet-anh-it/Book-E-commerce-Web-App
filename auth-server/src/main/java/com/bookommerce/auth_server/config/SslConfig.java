package com.bookommerce.auth_server.config;

import java.io.IOException;

import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import jakarta.annotation.PostConstruct;

@Configuration
public class SslConfig {

    @PostConstruct
    public void configureSSL() {
        try {
            // Lấy đường dẫn tuyệt đối tới file .p12 trong thư mục resources
            ClassPathResource resource = new ClassPathResource("bookommerce-ssl.p12");
            String path = resource.getFile().getAbsolutePath();

            // Thiết lập TrustStore cho JVM
            // Điều này bảo Java: "Hãy tin tất cả chứng chỉ nằm trong file này"
            System.setProperty("javax.net.ssl.trustStore", path);
            System.setProperty("javax.net.ssl.trustStorePassword", "123456");
            System.setProperty("javax.net.ssl.trustStoreType", "PKCS12");

            // Log để kiểm tra
            System.out.println(">>> SSL TrustStore configured: " + path);
        } catch (IOException e) {
            throw new RuntimeException("Failed to load SSL Keystore for TrustStore", e);
        }
    }
}
