package com.bookommerce.auth_server.custom;

import java.util.Optional;

import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;

import com.bookommerce.auth_server.entity.User;
import com.bookommerce.auth_server.repository.UserRepository;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CustomOidcUserService implements OAuth2UserService<OidcUserRequest, OidcUser> {

    OidcUserService delegate = new OidcUserService();
    UserRepository userRepository;

    @Override
    public OidcUser loadUser(OidcUserRequest userRequest) throws OAuth2AuthenticationException {
        OidcUser oidcUser = this.delegate.loadUser(userRequest);
        Optional<User> optionalUser = this.userRepository.findByEmail(oidcUser.getEmail());
        if (optionalUser.isEmpty()) {
            User user = new User();
            user.setEmail(oidcUser.getEmail());
            this.userRepository.save(user);
        }
        return oidcUser;
    }
}
