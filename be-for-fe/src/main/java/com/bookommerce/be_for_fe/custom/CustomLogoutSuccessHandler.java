package com.bookommerce.be_for_fe.custom;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.client.oidc.web.logout.OidcClientInitiatedLogoutSuccessHandler;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;

// @formatter:off
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CustomLogoutSuccessHandler implements LogoutSuccessHandler {

    OidcClientInitiatedLogoutSuccessHandler delegate;
    OAuth2AuthorizedClientService oAuth2AuthorizedClientService;

 
    public CustomLogoutSuccessHandler(ClientRegistrationRepository clientRegistrationRepository, OAuth2AuthorizedClientService oAuth2AuthorizedClientService) {
        this.delegate = new OidcClientInitiatedLogoutSuccessHandler(clientRegistrationRepository);
        this.oAuth2AuthorizedClientService = oAuth2AuthorizedClientService;
    }

    @Override
    public void onLogoutSuccess(
        HttpServletRequest request,
        HttpServletResponse response,
        Authentication authentication) throws IOException, ServletException {
        OAuth2AuthenticationToken oauth2AuthenticationToken = (OAuth2AuthenticationToken) authentication;
        this.oAuth2AuthorizedClientService.removeAuthorizedClient(
            oauth2AuthenticationToken.getAuthorizedClientRegistrationId(),
            oauth2AuthenticationToken.getName());
        Set<GrantedAuthority> authorities = new HashSet<>(authentication.getAuthorities());
        SimpleGrantedAuthority roleCustomer = new SimpleGrantedAuthority("ROLE_CUSTOMER");
        if (authorities.contains(roleCustomer)) {
            this.delegate.setPostLogoutRedirectUri("https://app.bookommerce.com:8080");
            this.delegate.onLogoutSuccess(request, response, authentication);
        } else {
            this.delegate.setPostLogoutRedirectUri("https://auth.bookommerce.com:8282/page/store-login");
            this.delegate.onLogoutSuccess(request, response, authentication);
            
        }
    }
}
