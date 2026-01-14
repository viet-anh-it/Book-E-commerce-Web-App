package com.bookommerce.resource_server.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.lang.NonNull;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;

@Configuration
@FieldDefaults(level = AccessLevel.PRIVATE)
public class WebConfig {

    @Value(value = "${upload.image.book}")
    String bookImageUploadDir;

    @Value(value = "${upload.image.avatar}")
    String avatarImageUploadDir;

    @Bean
    public WebMvcConfigurer webMvcConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addResourceHandlers(@NonNull ResourceHandlerRegistry registry) {
                registry.addResourceHandler("/images/books/**")
                        .addResourceLocations("file:" + bookImageUploadDir);
                registry.addResourceHandler("/images/avatars/**")
                        .addResourceLocations("file:" + avatarImageUploadDir);
            }
        };
    }
}
