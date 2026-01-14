package com.bookommerce.be_for_fe.config;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.lang.NonNull;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.authority.mapping.GrantedAuthoritiesMapper;
import org.springframework.security.oauth2.client.JdbcOAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.registration.InMemoryClientRegistrationRepository;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.oauth2.core.oidc.IdTokenClaimNames;
import org.springframework.security.oauth2.core.oidc.user.OidcUserAuthority;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.csrf.CsrfTokenRepository;
import org.springframework.security.web.csrf.CsrfTokenRequestAttributeHandler;
import org.springframework.security.web.csrf.CsrfTokenRequestHandler;
import org.springframework.security.web.servlet.util.matcher.PathPatternRequestMatcher;
import org.springframework.security.web.util.matcher.MediaTypeRequestMatcher;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.bookommerce.be_for_fe.custom.CustomAuthenticationSuccessHandler;
import com.bookommerce.be_for_fe.custom.CustomLogoutSuccessHandler;
import com.bookommerce.be_for_fe.interceptor.SessionCookieMaxAgeSlidingInterceptor;

import jakarta.servlet.DispatcherType;

// @formatter:off
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private static final String FE_BASE_URL = "https://app.bookommerce.com:8080";
    private static final String API_GATEWAY_BASE_URL = "https://bff.bookommerce.com:8181";
    private static final String AUTH_SERVER_BASE_URL = "https://auth.bookommerce.com:8282";
    private static final String ADMIN_FE_BASE_URL = "https://admin.bookommerce.com:7979";

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http, LogoutSuccessHandler logoutSuccessHandler) throws Exception {
        http
            .authorizeHttpRequests((authorize) -> authorize
                // authorization for book
                .requestMatchers(HttpMethod.GET, "/api/books").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/books/{id}").permitAll()
                .requestMatchers(HttpMethod.POST, "/protected/api/books").hasAuthority("ROLE_PRODUCT_MANAGER")
                .requestMatchers(HttpMethod.PUT, "/protected/api/books/{id}").hasAuthority("ROLE_PRODUCT_MANAGER")
                .requestMatchers(HttpMethod.DELETE, "/protected/api/books/{id}").hasAuthority("ROLE_PRODUCT_MANAGER")
                // authorization for genre
                .requestMatchers("/api/genres/**").permitAll()
                // authorization for rating
                .requestMatchers(HttpMethod.GET, "/protected/api/ratings").hasAuthority("ROLE_PRODUCT_MANAGER")
                .requestMatchers(HttpMethod.GET, "/api/books/{bookId}/ratings").permitAll()
                .requestMatchers(HttpMethod.POST, "/protected/api/ratings").hasAuthority("ROLE_CUSTOMER")
                .requestMatchers(HttpMethod.PUT, "/protected/api/ratings").hasAuthority("ROLE_CUSTOMER")
                .requestMatchers(HttpMethod.PATCH, "/protected/api/ratings/{id}/approve").hasAuthority("ROLE_PRODUCT_MANAGER")
                .requestMatchers(HttpMethod.PATCH, "/protected/api/ratings/{id}/reject").hasAuthority("ROLE_PRODUCT_MANAGER")
                .requestMatchers(HttpMethod.DELETE, "/protected/api/ratings/{id}").hasAnyAuthority("ROLE_PRODUCT_MANAGER", "ROLE_CUSTOMER")
                // authorization for cart
                .requestMatchers(HttpMethod.GET, "/protected/api/carts").hasAuthority("ROLE_CUSTOMER")
                .requestMatchers(HttpMethod.POST, "/protected/api/carts/items").hasAuthority("ROLE_CUSTOMER")
                .requestMatchers(HttpMethod.PATCH, "/protected/api/carts/items/{cartItemId}").hasAuthority("ROLE_CUSTOMER")
                .requestMatchers(HttpMethod.DELETE, "/protected/api/carts/items/{cartItemId}").hasAuthority("ROLE_CUSTOMER")
                // authorization for profile
                .requestMatchers(HttpMethod.GET, "/protected/api/me").authenticated()
                .requestMatchers(HttpMethod.GET, "/protected/api/me/profile/avatar").authenticated()
                .requestMatchers(HttpMethod.GET, "/protected/api/me/profile").hasAuthority("ROLE_CUSTOMER")
                .requestMatchers(HttpMethod.PATCH, "/protected/api/me/profile").hasAuthority("ROLE_CUSTOMER")
                // authorization for image
                .requestMatchers(HttpMethod.GET, "/images/books/**", "/images/avatars/**").permitAll()
                .dispatcherTypeMatchers(DispatcherType.ERROR).permitAll()
                .anyRequest().authenticated())
            .oauth2Login(oauth2LoginConfigurer -> oauth2LoginConfigurer
                .authorizationEndpoint(authorizationEnpointConfigurer -> authorizationEnpointConfigurer
                    .baseUri("/protected/oauth2/authorization"))
                .redirectionEndpoint(redirectionEndpointConfigurer -> redirectionEndpointConfigurer
                    .baseUri("/protected/login/oauth2/code/*"))
                .successHandler(new CustomAuthenticationSuccessHandler()))
            .logout(logoutConfigurer -> logoutConfigurer
                .logoutUrl("/protected/logout")
                .logoutSuccessHandler(logoutSuccessHandler))
            .sessionManagement(sessionManagementConfigurer -> sessionManagementConfigurer
                .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED))
            .exceptionHandling(exceptionHandlingConfigurer -> exceptionHandlingConfigurer
                .defaultAuthenticationEntryPointFor(
                    new LoginUrlAuthenticationEntryPoint(AUTH_SERVER_BASE_URL + "/page/login"),
                    new MediaTypeRequestMatcher(MediaType.TEXT_HTML))
                .defaultAuthenticationEntryPointFor(
                    new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED),
                    new MediaTypeRequestMatcher(MediaType.APPLICATION_JSON)))
            .csrf(csrfConfigurer -> csrfConfigurer
                .csrfTokenRepository(this.csrfTokenRepository())
                .csrfTokenRequestHandler(this.csrfTokenRequestHandler())
                .requireCsrfProtectionMatcher(PathPatternRequestMatcher.withDefaults().matcher(HttpMethod.POST, "/protected/logout")))
            .cors(Customizer.withDefaults());
        return http.build();
    }

    @Bean
    public ClientRegistrationRepository clientRegistrationRepository() {
        return new InMemoryClientRegistrationRepository(this.googleClientRegistration());
    }

    private ClientRegistration googleClientRegistration() {
        return ClientRegistration.withRegistrationId("bff")
                .clientId("bff")
                .clientSecret("secret")
                .clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_BASIC)
                .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
                .redirectUri(API_GATEWAY_BASE_URL + "/protected/login/oauth2/code/bff")
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

            @Override
            public void addInterceptors(@NonNull InterceptorRegistry registry) {
                registry.addInterceptor(new SessionCookieMaxAgeSlidingInterceptor())
                        .addPathPatterns("/protected/**");
            }
        };
    }

    @Bean
    public LogoutSuccessHandler oidcLogoutSuccessHandler(
        ClientRegistrationRepository clientRegistrationRepository,
        OAuth2AuthorizedClientService oAuth2AuthorizedClientService) {
        return new CustomLogoutSuccessHandler(clientRegistrationRepository, oAuth2AuthorizedClientService);
    }

    @Bean
	public GrantedAuthoritiesMapper userAuthoritiesMapper() {
		return (authorities) -> {
			Set<GrantedAuthority> mappedAuthorities = new HashSet<>();
			authorities.forEach(authority -> {
                if (authority instanceof OidcUserAuthority oidcUserAuthority)  {
				    oidcUserAuthority.getIdToken()
                        .getClaimAsStringList("authorities")
                        .forEach(auth -> mappedAuthorities.add(new SimpleGrantedAuthority(auth)));
                }
			});
			return mappedAuthorities;
		};
	}

    @Bean
    public OAuth2AuthorizedClientService oAuth2AuthorizedClientService(
        JdbcTemplate jdbcTemplate,
        ClientRegistrationRepository clientRegistrationRepository) {
        return new JdbcOAuth2AuthorizedClientService(jdbcTemplate, clientRegistrationRepository);
    }

    @Bean
    public CsrfTokenRepository csrfTokenRepository() {
        CookieCsrfTokenRepository repository =  new CookieCsrfTokenRepository();
        repository.setCookieName("XSRF-TOKEN");
        repository.setHeaderName("X-XSRF-TOKEN");
        repository.setParameterName("_csrf");
        repository.setCookieCustomizer(cookie -> cookie
            .domain("bookommerce.com")
            .secure(true)
            .sameSite("Strict")
            .maxAge(-1)
            .path("/")
            .httpOnly(false));
        return repository;
    }

    @Bean
    public CsrfTokenRequestHandler csrfTokenRequestHandler() {
        CsrfTokenRequestAttributeHandler handler = new CsrfTokenRequestAttributeHandler();
        handler.setCsrfRequestAttributeName(null);
        return handler;
    }
}
