package com.bookommerce.resource_server.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.core.GrantedAuthorityDefaults;
import org.springframework.security.config.http.SessionCreationPolicy;
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
    public SecurityFilterChain securityFilterChain(
        HttpSecurity http
        /*OpaqueTokenAuthenticationConverter opaqueTokenAuthenticationConverter*/) throws Exception {
		http
            .oauth2ResourceServer(oauth2ResourceServerConfigurer -> oauth2ResourceServerConfigurer
                // .opaqueToken(opaqueTokenConfigurer -> opaqueTokenConfigurer
                //     .introspector(opaqueTokenIntrospector())
                //     .authenticationConverter(opaqueTokenAuthenticationConverter)))
                .jwt(jwtConfigurer -> jwtConfigurer
                    .jwkSetUri(AUTH_SERVER_BASE_URL + "/oauth2/jwks")
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
                // authorization for cart
                .requestMatchers(HttpMethod.GET, "/api/carts").hasAuthority("ROLE_CUSTOMER")
                .requestMatchers(HttpMethod.POST, "/api/carts/items").hasAuthority("ROLE_CUSTOMER")
                .requestMatchers(HttpMethod.PATCH, "/api/carts/items/{cartItemId}").hasAuthority("ROLE_CUSTOMER")
                .requestMatchers(HttpMethod.DELETE, "/api/carts/items/{cartItemId}").hasAuthority("ROLE_CUSTOMER")
                // authorization for profile
                .requestMatchers(HttpMethod.PATCH, "/api/me/profile/avatar").hasAuthority("ROLE_CUSTOMER")
                .requestMatchers(HttpMethod.GET, "/api/me/profile").hasAuthority("ROLE_CUSTOMER")
                .requestMatchers(HttpMethod.PATCH, "/api/me/profile").hasAuthority("ROLE_CUSTOMER")
                // authorization for image
                .requestMatchers(HttpMethod.GET, "/images/books/**", "/images/avatars/**").permitAll()
                .dispatcherTypeMatchers(DispatcherType.ERROR).permitAll()
				.anyRequest().authenticated())
            .sessionManagement(sessionManagementConfigurer -> sessionManagementConfigurer
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .csrf(csrf -> csrf.disable())
            .cors(cors -> cors.disable());
        return http.build();
    }

    // @Bean
    // public JwtDecoder jwtDecoder() {
    //     return JwtDecoders.fromIssuerLocation(AUTH_SERVER_BASE_URL);
    // }

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

    // @Bean
    // public OpaqueTokenIntrospector opaqueTokenIntrospector() {
    //     return SpringOpaqueTokenIntrospector.withIntrospectionUri("https://auth.bookommerce.com:8282/oauth2/introspect")
    //         .clientId("resource-server")
    //         .clientSecret("secret")
    //         .build();
    // }

    // @Bean
    // public OpaqueTokenAuthenticationConverter opaqueTokenAuthenticationConverter() {
    //     return (introspectedToken, authenticatedPrincipal) -> {
    //         Map<String, Object> attributes = authenticatedPrincipal.getAttributes();
    //         Collection<String> authoritiesClaim = (Collection<String>) attributes.get("authorities");
    //         Collection<GrantedAuthority> grantedAuthorities = authoritiesClaim.stream()
    //             .map(authority -> new SimpleGrantedAuthority(authority))
    //             .collect(Collectors.toList());
    //         Instant iat = authenticatedPrincipal.getAttribute(OAuth2TokenIntrospectionClaimNames.IAT);
	// 	    Instant exp = authenticatedPrincipal.getAttribute(OAuth2TokenIntrospectionClaimNames.EXP);
	// 	    OAuth2AccessToken accessToken = new OAuth2AccessToken(OAuth2AccessToken.TokenType.BEARER, introspectedToken, iat, exp);
	// 	    return new BearerTokenAuthentication(authenticatedPrincipal, accessToken, grantedAuthorities);
    //     };
    // }
}
