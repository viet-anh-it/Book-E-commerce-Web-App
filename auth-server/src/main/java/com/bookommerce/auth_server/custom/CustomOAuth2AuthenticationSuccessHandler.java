package com.bookommerce.auth_server.custom;

import java.io.IOException;

import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.server.authorization.authentication.OAuth2AccessTokenAuthenticationToken;
import org.springframework.security.oauth2.server.authorization.web.authentication.OAuth2AccessTokenResponseAuthenticationSuccessHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CustomOAuth2AuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    //@formatter:off
    @Override
    @SuppressWarnings("null")
    public void onAuthenticationSuccess(
        HttpServletRequest request,
        HttpServletResponse response,
        Authentication authentication) throws IOException, ServletException {
        if(authentication instanceof OAuth2AccessTokenAuthenticationToken) {
            OAuth2AccessTokenAuthenticationToken oauth2AccessTokenAuthenticationToken = 
                (OAuth2AccessTokenAuthenticationToken) authentication;
            String accessTokenValue = oauth2AccessTokenAuthenticationToken.getAccessToken().getTokenValue();
            log.info(">>>>>>>>>>>>>>>>>>>> Access token value: {}", accessTokenValue);
            String refreshTokenValue = oauth2AccessTokenAuthenticationToken.getRefreshToken().getTokenValue();
            log.info(">>>>>>>>>>>>>>>>>>>> Refresh token value: {}", refreshTokenValue);
        }
        OAuth2AccessTokenResponseAuthenticationSuccessHandler defaultHandler = 
            new OAuth2AccessTokenResponseAuthenticationSuccessHandler();
        defaultHandler.onAuthenticationSuccess(request, response, authentication);
    }

}
