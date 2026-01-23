package com.bookommerce.resource_server.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

// @formatter:off
@Configuration
public class RedisConfig {

    @Bean
    public RedisConnectionFactory redisConnectionFactory() {
        RedisStandaloneConfiguration config =
            new RedisStandaloneConfiguration("localhost", 6379);
        return new LettuceConnectionFactory(config);
    }


    // @Bean
    // public RedisTemplate<String, String> redisTemplate(RedisConnectionFactory connectionFactory) {
    //     RedisTemplate<String, String> template = new RedisTemplate<>();
    //     template.setConnectionFactory(connectionFactory);

    //     StringRedisSerializer stringRedisSerializer = new StringRedisSerializer();
    //     template.setKeySerializer(stringRedisSerializer);
    //     template.setValueSerializer(stringRedisSerializer);
    //     template.setHashKeySerializer(stringRedisSerializer);  
    //     template.setHashValueSerializer(stringRedisSerializer);

    //     template.afterPropertiesSet();
        
    //     return template;
    // }

    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);

        StringRedisSerializer stringRedisSerializer = new StringRedisSerializer();
        GenericJackson2JsonRedisSerializer genericJackson2JsonRedisSerializer = 
            new GenericJackson2JsonRedisSerializer();
        template.setKeySerializer(stringRedisSerializer);
        template.setValueSerializer(genericJackson2JsonRedisSerializer);
        template.setHashKeySerializer(stringRedisSerializer);  
        template.setHashValueSerializer(genericJackson2JsonRedisSerializer);

        template.afterPropertiesSet();
        
        return template;
    }
}
