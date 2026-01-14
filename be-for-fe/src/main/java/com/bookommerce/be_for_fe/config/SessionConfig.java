package com.bookommerce.be_for_fe.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.session.jdbc.config.annotation.web.http.EnableJdbcHttpSession;
import org.springframework.session.web.http.CookieSerializer;
import org.springframework.session.web.http.DefaultCookieSerializer;

@Configuration
@EnableJdbcHttpSession(maxInactiveIntervalInSeconds = 60 * 60 * 24 * 30)
public class SessionConfig {

    @Bean
    public CookieSerializer cookieSerializer() {
        DefaultCookieSerializer serializer = new DefaultCookieSerializer();
        serializer.setCookieName("BFF-SESSION");
        serializer.setCookiePath("/protected");
        serializer.setDomainName("bff.bookommerce.com");
        serializer.setUseHttpOnlyCookie(true);
        serializer.setUseSecureCookie(true);
        serializer.setSameSite("Lax");
        serializer.setCookieMaxAge(60 * 60 * 24 * 30);
        serializer.setUseBase64Encoding(false);
        return serializer;
    }
}
