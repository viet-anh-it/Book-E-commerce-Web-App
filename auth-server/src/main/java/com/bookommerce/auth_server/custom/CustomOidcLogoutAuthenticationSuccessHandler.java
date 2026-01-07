package com.bookommerce.auth_server.custom;

import java.io.IOException;

import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.oidc.endpoint.OidcParameterNames;
import org.springframework.security.oauth2.server.authorization.OAuth2Authorization;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationService;
import org.springframework.security.oauth2.server.authorization.OAuth2TokenType;
import org.springframework.security.oauth2.server.authorization.oidc.authentication.OidcLogoutAuthenticationToken;
import org.springframework.security.oauth2.server.authorization.oidc.web.authentication.OidcLogoutAuthenticationSuccessHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;

// @formatter:off
@Component
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CustomOidcLogoutAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    OidcLogoutAuthenticationSuccessHandler delegate;
    OAuth2AuthorizationService oAuth2AuthorizationService;

    public CustomOidcLogoutAuthenticationSuccessHandler(OAuth2AuthorizationService oAuth2AuthorizationService) {
        this.delegate = new OidcLogoutAuthenticationSuccessHandler();
        this.oAuth2AuthorizationService = oAuth2AuthorizationService;
    }

    @Override
    public void onAuthenticationSuccess(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication) throws IOException, ServletException {
        OidcLogoutAuthenticationToken oidcLogoutAuthenticationToken = (OidcLogoutAuthenticationToken) authentication;
        String idTokenHint = oidcLogoutAuthenticationToken.getIdTokenHint();
        OAuth2Authorization oAuth2Authorization = 
            this.oAuth2AuthorizationService.findByToken(idTokenHint, new OAuth2TokenType(OidcParameterNames.ID_TOKEN));
        this.oAuth2AuthorizationService.remove(oAuth2Authorization);
        this.delegate.onAuthenticationSuccess(request, response, authentication);
    }

}
