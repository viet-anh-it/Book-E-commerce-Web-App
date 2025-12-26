package com.bookommerce.auth_server.config;

import java.io.InputStream;
import java.security.Key;
import java.security.KeyStore;
import java.security.PublicKey;
import java.security.cert.Certificate;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.time.Duration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.registration.InMemoryClientRegistrationRepository;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.oauth2.core.oidc.OidcScopes;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.server.authorization.client.InMemoryRegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.config.annotation.web.configuration.OAuth2AuthorizationServerConfiguration;
import org.springframework.security.oauth2.server.authorization.config.annotation.web.configurers.OAuth2AuthorizationServerConfigurer;
import org.springframework.security.oauth2.server.authorization.settings.AuthorizationServerSettings;
import org.springframework.security.oauth2.server.authorization.settings.ClientSettings;
import org.springframework.security.oauth2.server.authorization.settings.OAuth2TokenFormat;
import org.springframework.security.oauth2.server.authorization.settings.TokenSettings;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.security.web.util.matcher.MediaTypeRequestMatcher;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.bookommerce.auth_server.custom.CustomOAuth2AuthenticationSuccessHandler;
import com.bookommerce.auth_server.custom.CustomOidcUserService;
import com.bookommerce.auth_server.entity.User;
import com.bookommerce.auth_server.interceptor.LoginSignupPageInterceptor;
import com.bookommerce.auth_server.repository.UserRepository;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.ImmutableJWKSet;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;

import jakarta.servlet.DispatcherType;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private static final String FE_BASE_URL = "https://app.bookommerce.com:8080";
    private static final String API_GATEWAY_BASE_URL = "https://bff.bookommerce.com:8181";
    private static final String AUTH_SERVER_BASE_URL = "https://auth.bookommerce.com:8282";

    @Bean
    @Order(1)
    public SecurityFilterChain authorizationServerSecurityFilterChain(HttpSecurity http) throws Exception {
        //@formatter:off
        OAuth2AuthorizationServerConfigurer authorizationServerConfigurer = 
            OAuth2AuthorizationServerConfigurer.authorizationServer();
        http
            .securityMatcher(authorizationServerConfigurer.getEndpointsMatcher())
            .with(authorizationServerConfigurer, (authorizationServer) -> authorizationServer
                .oidc(Customizer.withDefaults())
                .tokenEndpoint(oauth2TokenEndpointConfigurer -> oauth2TokenEndpointConfigurer
                    .accessTokenResponseHandler(new CustomOAuth2AuthenticationSuccessHandler())))
            .authorizeHttpRequests((authorize) -> authorize.anyRequest().authenticated())
            .securityContext(securityContextConfigurer -> securityContextConfigurer
                .securityContextRepository(this.securityContextRepository()))
            .exceptionHandling(exceptionHandlingConfigurer -> exceptionHandlingConfigurer
                .authenticationEntryPoint(new LoginUrlAuthenticationEntryPoint("/page/login")));
        return http.build();
        //@formatter:on
    }

    @Bean
    @Order(2)
    public SecurityFilterChain defaultSecurityFilterChain(HttpSecurity http) throws Exception {
        //@formatter:off
        http
            .authorizeHttpRequests((authorize) -> authorize
                .requestMatchers(HttpMethod.GET, "/page/signup").permitAll()                                                                
                .requestMatchers(HttpMethod.POST, "/api/register").permitAll()
                .requestMatchers(HttpMethod.GET, "/page/login").permitAll()
                .requestMatchers(HttpMethod.POST, "/api/login").permitAll()
                .dispatcherTypeMatchers(DispatcherType.ERROR).permitAll()
                .anyRequest().authenticated())
            .securityContext(securityContextConfigurer -> securityContextConfigurer
                .securityContextRepository(this.securityContextRepository()))
            .sessionManagement(sessionManagementConfigurer -> sessionManagementConfigurer
                .sessionCreationPolicy(SessionCreationPolicy.ALWAYS))
            .exceptionHandling(exceptionHandlingConfigurer -> exceptionHandlingConfigurer
                .defaultAuthenticationEntryPointFor(
                    new LoginUrlAuthenticationEntryPoint("/page/login"),
                    new MediaTypeRequestMatcher(MediaType.TEXT_HTML))
                .defaultAuthenticationEntryPointFor(
                    new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED),
                    new MediaTypeRequestMatcher(MediaType.APPLICATION_JSON)))
            .oauth2Login(oauth2LoginConfigurer -> oauth2LoginConfigurer
                .loginPage("/page/login")
                .defaultSuccessUrl(API_GATEWAY_BASE_URL + "/oauth2/authorization/bff", true))
            .formLogin(formLoginConfigurer -> formLoginConfigurer.disable())
            .httpBasic(httpBasicConfigurer -> httpBasicConfigurer.disable())
            .csrf(Customizer.withDefaults())
            .cors(Customizer.withDefaults());
        return http.build();
        //@formatter:on
    }

    @Bean
    public UserDetailsService userDetailsService(UserRepository userRepository) throws UsernameNotFoundException {
        //@formatter:off
        return username -> {
            User user = userRepository.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
            return new org.springframework.security.core.userdetails.User(
                user.getEmail(), user.getPassword(), AuthorityUtils.NO_AUTHORITIES);
        };
        //@formatter:on
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    //@formatter:off
    @Bean
    public RegisteredClientRepository registeredClientRepository() {
        RegisteredClient bff = RegisteredClient.withId("bff")
                .clientId("bff")
                .clientSecret("$2a$12$zBuKEpT5/7BJ/d7ZcoOGmepGXPdoZOx17VieNDn35cZMhnSMDlPT.")
                .clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_BASIC)
                .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
                .authorizationGrantType(AuthorizationGrantType.REFRESH_TOKEN)
                .redirectUri(API_GATEWAY_BASE_URL + "/login/oauth2/code/bff")
                .postLogoutRedirectUri(FE_BASE_URL)
                .scope(OidcScopes.OPENID)
                .scope(OidcScopes.PROFILE)
                .clientSettings(ClientSettings.builder()
                    .requireAuthorizationConsent(false)
                    .build())
                .tokenSettings(TokenSettings.builder()
                    .accessTokenFormat(OAuth2TokenFormat.SELF_CONTAINED)
                    .accessTokenTimeToLive(Duration.ofDays(365))
                    .refreshTokenTimeToLive(Duration.ofDays(365))
                    .reuseRefreshTokens(false)
                    .build())
                .build();

        return new InMemoryRegisteredClientRepository(bff);
    }

    @Bean
    public JWKSource<SecurityContext> jwkSource() {
        try {
            String alias = "bookommerce-token-key";
            char[] password = "123456".toCharArray();

            // Load keystore từ resources
            ClassPathResource resource = new ClassPathResource("bookommerce-token-key.p12");

            KeyStore keyStore = KeyStore.getInstance("PKCS12");

            try (InputStream inputStream = resource.getInputStream()) {
                keyStore.load(inputStream, password);
            }

            // Lấy private key
            Key key = keyStore.getKey(alias, password);
            if (!(key instanceof RSAPrivateKey privateKey)) {
                throw new IllegalStateException("Key is not RSA private key");
            }

            // Lấy public key từ certificate
            Certificate cert = keyStore.getCertificate(alias);
            PublicKey publicKey = cert.getPublicKey();
            if (!(publicKey instanceof RSAPublicKey rsaPublicKey)) {
                throw new IllegalStateException("Certificate does not contain RSA public key");
            }

            RSAKey rsaKey = new RSAKey.Builder(rsaPublicKey)
                    .privateKey(privateKey)
                    .keyID(alias)
                    .build();

            return new ImmutableJWKSet<>(new JWKSet(rsaKey));

        } catch (Exception ex) {
            throw new IllegalStateException("Failed to load RSA key from classpath", ex);
        }
    }

    @Bean
    public JwtDecoder jwtDecoder(JWKSource<SecurityContext> jwkSource) {
        return OAuth2AuthorizationServerConfiguration.jwtDecoder(jwkSource);
    }

    @Bean
    public AuthorizationServerSettings authorizationServerSettings() {
        return AuthorizationServerSettings.builder().build();
    }

    //@formatter:off
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public SecurityContextRepository securityContextRepository() {
        return new HttpSessionSecurityContextRepository();
    }

    @Bean
    public WebMvcConfigurer webMvcConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addInterceptors(@NonNull InterceptorRegistry registry) {
                registry.addInterceptor(new LoginSignupPageInterceptor())
                    .addPathPatterns("/page/login")
                    .addPathPatterns("/page/signup");
            }

            @Override
            public void addCorsMappings(@NonNull CorsRegistry registry) {
                registry.addMapping("/api/me")
                    .allowedOrigins(FE_BASE_URL)
                    .allowedMethods(HttpMethod.GET.name())
                    .allowedHeaders("*")
                    .allowCredentials(true);
            }
        };
    }

    @Bean
	public ClientRegistrationRepository clientRegistrationRepository() {
		return new InMemoryClientRegistrationRepository(this.googleClientRegistration());
	}

    private ClientRegistration googleClientRegistration() {
		return ClientRegistration.withRegistrationId("google")
			.clientId("722886029697-prvrbn20cvrv7l5mgqviu5a35rjrco1p.apps.googleusercontent.com")
			.clientSecret("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")
			.clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_BASIC)
			.authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
			.redirectUri(AUTH_SERVER_BASE_URL + "/login/oauth2/code/google")
			.scope(OidcScopes.OPENID, OidcScopes.PROFILE, OidcScopes.EMAIL, OidcScopes.ADDRESS, OidcScopes.PHONE)
			.authorizationUri("https://accounts.google.com/o/oauth2/v2/auth")
			.tokenUri("https://www.googleapis.com/oauth2/v4/token")
			.userInfoUri("https://www.googleapis.com/oauth2/v3/userinfo")
			.userNameAttributeName(OidcScopes.EMAIL)
			.jwkSetUri("https://www.googleapis.com/oauth2/v3/certs")
			.clientName("Google")
			.build();
	}

    @Bean
    public OAuth2UserService<OidcUserRequest, OidcUser> oidcUserService(UserRepository userRepository) {
        return new CustomOidcUserService(userRepository);
    }
}