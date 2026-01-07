package com.bookommerce.auth_server.custom;

import java.util.Optional;
import java.util.Set;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;

import com.bookommerce.auth_server.constant.Roles;
import com.bookommerce.auth_server.dto.event.RegistrationSuccessEvent;
import com.bookommerce.auth_server.entity.Role;
import com.bookommerce.auth_server.entity.User;
import com.bookommerce.auth_server.repository.RoleRepository;
import com.bookommerce.auth_server.repository.UserRepository;
import com.bookommerce.auth_server.service.event.RegistrationSuccessEventPublisher;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

// @formatter:off
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CustomOidcUserService implements OAuth2UserService<OidcUserRequest, OidcUser> {

    OidcUserService delegate = new OidcUserService();
    UserRepository userRepository;
    RoleRepository roleRepository;
    RegistrationSuccessEventPublisher registrationSuccessEventPublisher;

    @Override
    public OidcUser loadUser(OidcUserRequest userRequest) throws OAuth2AuthenticationException {
        OidcUser loadedOidcUser = this.delegate.loadUser(userRequest);
        Optional<User> optionalUser = this.userRepository.findByEmail(loadedOidcUser.getEmail());
        if (optionalUser.isEmpty()) {
            User user = new User();
            user.setEmail(loadedOidcUser.getEmail());
            Role role = this.roleRepository.findByName(Roles.ROLE_CUSTOMER.name());
            user.setRoles(Set.of(role));
            this.userRepository.save(user);
            RegistrationSuccessEvent registrationSuccessEvent = new RegistrationSuccessEvent(user.getEmail());
            this.registrationSuccessEventPublisher.publishRegistrationSuccessEvent(registrationSuccessEvent);
        }
        OidcUser oidcUser = new DefaultOidcUser(
            Set.of(new SimpleGrantedAuthority("ROLE_CUSTOMER")), 
            loadedOidcUser.getIdToken(), 
            "email");
        return oidcUser;
    }
}
