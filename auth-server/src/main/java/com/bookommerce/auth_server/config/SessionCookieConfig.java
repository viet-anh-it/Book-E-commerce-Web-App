package com.bookommerce.auth_server.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.session.web.http.CookieSerializer;
import org.springframework.session.web.http.DefaultCookieSerializer;

@Slf4j
@Configuration
public class SessionCookieConfig {

    @Bean
    public CookieSerializer cookieSerializer() {
        DefaultCookieSerializer serializer = new DefaultCookieSerializer();
        serializer.setCookieName("AUTH-SESSION");
        serializer.setUseSecureCookie(true);
        serializer.setSameSite("None");
        serializer.setCookieMaxAge(60 * 60 * 24 * 365);
        log.info(">>>>>>>>>>>>>>>>>>>> CookieSerializer configured");
        return serializer;
    }
}
