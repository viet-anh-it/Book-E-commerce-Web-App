package com.bookommerce.be_for_fe.config;

import java.util.Map;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.lang.NonNull;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.oauth2.client.oidc.web.logout.OidcClientInitiatedLogoutSuccessHandler;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.registration.InMemoryClientRegistrationRepository;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.oauth2.core.oidc.IdTokenClaimNames;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.security.web.servlet.util.matcher.PathPatternRequestMatcher;
import org.springframework.security.web.util.matcher.MediaTypeRequestMatcher;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import jakarta.servlet.DispatcherType;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private static final String FE_BASE_URL = "https://app.bookommerce.com:8080";
    private static final String API_GATEWAY_BASE_URL = "https://bff.bookommerce.com:8181";
    private static final String AUTH_SERVER_BASE_URL = "https://auth.bookommerce.com:8282";
    private static final String ADMIN_FE_BASE_URL = "https://admin.bookommerce.com:7979";

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        //@formatter:off
        http
            .authorizeHttpRequests((authorize) -> authorize
                .requestMatchers("/api/books/**").permitAll()
                .requestMatchers("/api/genres/**").permitAll()
                .requestMatchers("/api/ratings/**").permitAll()
                .requestMatchers("/csrf").permitAll()
                .requestMatchers(HttpMethod.GET, "/confirm-logout").permitAll()
                .requestMatchers(HttpMethod.GET, "/images/books/**").permitAll()
                .dispatcherTypeMatchers(DispatcherType.ERROR).permitAll()
                .anyRequest().authenticated())
            .oauth2Login(oauth2LoginConfigurer -> oauth2LoginConfigurer.defaultSuccessUrl(FE_BASE_URL))
            .logout(logoutConfigurer -> logoutConfigurer
                .logoutSuccessHandler(this.oidcLogoutSuccessHandler(this.clientRegistrationRepository())))
            .exceptionHandling(exceptionHandlingConfigurer -> exceptionHandlingConfigurer
                .defaultAuthenticationEntryPointFor(
                    new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED),
                    new MediaTypeRequestMatcher(MediaType.APPLICATION_JSON)))
            .csrf(csrfConfigurer -> csrfConfigurer.requireCsrfProtectionMatcher(
                PathPatternRequestMatcher.withDefaults().matcher(HttpMethod.POST, "/logout")))
            .cors(Customizer.withDefaults());
        //@formatter:on
        return http.build();
    }

    @Bean
    public ClientRegistrationRepository clientRegistrationRepository() {
        return new InMemoryClientRegistrationRepository(this.googleClientRegistration());
    }

    //@formatter:off
    private ClientRegistration googleClientRegistration() {
        return ClientRegistration.withRegistrationId("bff")
                .clientId("bff")
                .clientSecret("secret")
                .clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_BASIC)
                .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
                .redirectUri(API_GATEWAY_BASE_URL + "/login/oauth2/code/bff")
                .scope("openid", "profile")
                .authorizationUri(AUTH_SERVER_BASE_URL + "/oauth2/authorize")
                .tokenUri(AUTH_SERVER_BASE_URL + "/oauth2/token")
                .userInfoUri(AUTH_SERVER_BASE_URL + "/userinfo")
                .userNameAttributeName(IdTokenClaimNames.SUB)
                .jwkSetUri(AUTH_SERVER_BASE_URL + "/oauth2/jwks")
                .clientName("bff")
                .providerConfigurationMetadata(
                    Map.of("end_session_endpoint", AUTH_SERVER_BASE_URL + "/connect/logout"))
                .build();
    }

    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(@NonNull CorsRegistry registry) {
                registry.addMapping("/**")
                        .allowedOrigins(FE_BASE_URL, ADMIN_FE_BASE_URL)
                        .allowedMethods(
                                HttpMethod.GET.name(),
                                HttpMethod.POST.name(),
                                HttpMethod.PUT.name(),
                                HttpMethod.PATCH.name(),
                                HttpMethod.DELETE.name())
                        .allowCredentials(true)
                        .allowedHeaders("*");
            }
        };
    }

    //@formatter:off
    private LogoutSuccessHandler oidcLogoutSuccessHandler(ClientRegistrationRepository clientRegistrationRepository) {
        OidcClientInitiatedLogoutSuccessHandler oidcLogoutSuccessHandler = 
            new OidcClientInitiatedLogoutSuccessHandler(clientRegistrationRepository);
        oidcLogoutSuccessHandler.setPostLogoutRedirectUri(FE_BASE_URL);
        return oidcLogoutSuccessHandler;
    }
}
