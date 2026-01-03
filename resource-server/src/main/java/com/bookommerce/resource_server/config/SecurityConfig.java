package com.bookommerce.resource_server.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.core.GrantedAuthorityDefaults;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtDecoders;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.web.SecurityFilterChain;

import jakarta.servlet.DispatcherType;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;

// @formatter:off
@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class SecurityConfig {

    static String AUTH_SERVER_BASE_URL = "https://auth.bookommerce.com:8282";

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		http
            .oauth2ResourceServer(oauth2 -> oauth2.jwt(jwtConfigurer -> jwtConfigurer
                .jwtAuthenticationConverter(jwtAuthenticationConverter())))   
            .authorizeHttpRequests((authorize) -> authorize
                // authorization for book
                .requestMatchers(HttpMethod.GET, "/api/books").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/books/{id}").permitAll()
                .requestMatchers(HttpMethod.POST, "/api/books").hasAuthority("ROLE_PRODUCT_MANAGER")
                .requestMatchers(HttpMethod.PUT, "/api/books/{id}").hasAuthority("ROLE_PRODUCT_MANAGER")
                .requestMatchers(HttpMethod.DELETE, "/api/books/{id}").hasAuthority("ROLE_PRODUCT_MANAGER")
                // authorization for rating
                .requestMatchers(HttpMethod.GET, "/api/ratings").hasAuthority("ROLE_PRODUCT_MANAGER")
                .requestMatchers(HttpMethod.GET, "/api/books/{bookId}/ratings").permitAll()
                .requestMatchers(HttpMethod.POST, "/api/ratings").hasAuthority("ROLE_CUSTOMER")
                .requestMatchers(HttpMethod.PUT, "/api/ratings").hasAuthority("ROLE_CUSTOMER")
                .requestMatchers(HttpMethod.PATCH, "/api/ratings/{id}/approve").hasAuthority("ROLE_PRODUCT_MANAGER")
                .requestMatchers(HttpMethod.PATCH, "/api/ratings/{id}/reject").hasAuthority("ROLE_PRODUCT_MANAGER")
                .requestMatchers(HttpMethod.DELETE, "/api/ratings/{id}").hasAnyAuthority("ROLE_PRODUCT_MANAGER", "ROLE_CUSTOMER")
                // authorization for genre
                .requestMatchers("/api/genres/**").permitAll()
                // authorization for image
                .requestMatchers(HttpMethod.GET, "/images/books/**").permitAll()
                .dispatcherTypeMatchers(DispatcherType.ERROR).permitAll()
				.anyRequest().authenticated())
            .sessionManagement(sessionManagementConfigurer -> sessionManagementConfigurer
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .csrf(csrf -> csrf.disable())
            .cors(cors -> cors.disable());
        return http.build();
    }

    @Bean
    public JwtDecoder jwtDecoder() {
        return JwtDecoders.fromIssuerLocation(AUTH_SERVER_BASE_URL);
    }

    @Bean
    public GrantedAuthorityDefaults grantedAuthorityDefaults() {
        return new GrantedAuthorityDefaults("");
    }

    @Bean
    public JwtAuthenticationConverter jwtAuthenticationConverter() {
        JwtGrantedAuthoritiesConverter grantedAuthoritiesConverter = new JwtGrantedAuthoritiesConverter();
        grantedAuthoritiesConverter.setAuthoritiesClaimName("authorities");
        grantedAuthoritiesConverter.setAuthorityPrefix("");

        JwtAuthenticationConverter jwtAuthenticationConverter = new JwtAuthenticationConverter();
        jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(grantedAuthoritiesConverter);
        return jwtAuthenticationConverter;
    }
}
