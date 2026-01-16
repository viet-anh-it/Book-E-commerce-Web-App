package com.bookommerce.auth_server.service;

import java.io.IOException;
import java.time.Instant;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextHolderStrategy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.validation.BeanPropertyBindingResult;

import com.bookommerce.auth_server.constant.Roles;
import com.bookommerce.auth_server.dto.event.AccountActivationSuccessEvent;
import com.bookommerce.auth_server.dto.event.RegistrationSuccessEvent;
import com.bookommerce.auth_server.dto.request.LoginRequestDto;
import com.bookommerce.auth_server.dto.request.RegistrationRequestDto;
import com.bookommerce.auth_server.entity.AccountActivationToken;
import com.bookommerce.auth_server.entity.Role;
import com.bookommerce.auth_server.entity.User;
import com.bookommerce.auth_server.exception.AccessDeniedException;
import com.bookommerce.auth_server.exception.EmailAlreadyExistedException;
import com.bookommerce.auth_server.mapper.UserMapper;
import com.bookommerce.auth_server.repository.AccountActivationTokenRepository;
import com.bookommerce.auth_server.repository.RoleRepository;
import com.bookommerce.auth_server.repository.UserRepository;
import com.bookommerce.auth_server.service.event.AccountActivationSuccessEventPublisher;
import com.bookommerce.auth_server.service.event.RegistrationSuccessEventPublisher;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

// @formatter:off
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AuthService {

    UserRepository userRepository;
    UserMapper userMapper;
    PasswordEncoder passwordEncoder;
    AuthenticationManager authenticationManager;
    SecurityContextRepository securityContextRepository;
    RoleRepository roleRepository;
    RegistrationSuccessEventPublisher registrationSuccessEventPublisher;
    AccountActivationTokenRepository accountActivationTokenRepository;
    AccountActivationSuccessEventPublisher accountActivationSuccessEventPublisher;

    @Transactional
    public void register(RegistrationRequestDto registrationRequestDto) {
        if (this.userRepository.findByEmail(registrationRequestDto.email()).isPresent()) {
            BeanPropertyBindingResult bindingResult = new BeanPropertyBindingResult(registrationRequestDto, "registrationRequestDto");
            bindingResult.rejectValue("email", "email", "3:Email already existed");
            throw new EmailAlreadyExistedException(bindingResult);
        }
        User user = this.userMapper.toUser(registrationRequestDto);
        user.setPasswordHash(this.passwordEncoder.encode(registrationRequestDto.password()));
        Role role = this.roleRepository.findByName(Roles.ROLE_CUSTOMER.name());
        user.setRoles(Set.of(role));
        this.userRepository.save(user);
        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override
            public void afterCommit() {
                registrationSuccessEventPublisher.publishRegistrationSuccessEvent(new RegistrationSuccessEvent(user.getEmail()));
            }
        });
    }

    public Map<String, String> login(LoginRequestDto loginRequestDto, HttpServletRequest request, HttpServletResponse response) {
        UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = 
            new UsernamePasswordAuthenticationToken(loginRequestDto.email(), loginRequestDto.password());
        Authentication authentication = this.authenticationManager.authenticate(usernamePasswordAuthenticationToken);
        Set<GrantedAuthority> authorities = new HashSet<>(authentication.getAuthorities());
        SimpleGrantedAuthority roleCustomer = new SimpleGrantedAuthority(Roles.ROLE_CUSTOMER.name());
        boolean hasRoleCustomer = authorities.contains(roleCustomer);
        String customerLoginPath = "/api/login/customer";
        String storeLoginPath = "/api/login/store";
        String servletPath = request.getServletPath();
        boolean loginFromCustomerPage = servletPath.contains(customerLoginPath);
        boolean loginFromStorePage = servletPath.contains(storeLoginPath);
        if ((loginFromCustomerPage && !hasRoleCustomer) || (loginFromStorePage && hasRoleCustomer)) {
            throw new AccessDeniedException();
        }

        HttpSession httpSession = request.getSession(false);
        if (httpSession == null) {
            httpSession = request.getSession();
        } else {
            request.changeSessionId();
        }

        SecurityContextHolderStrategy securityContextHolderStrategy = SecurityContextHolder.getContextHolderStrategy();
        SecurityContext securityContext = securityContextHolderStrategy.createEmptyContext();
        securityContext.setAuthentication(authentication);
        securityContextHolderStrategy.setContext(securityContext);
        securityContextRepository.saveContext(securityContext, request, response);

        String redirectUrl = "https://bff.bookommerce.com:8181/protected/oauth2/authorization/bff";
        return Map.of("redirect", redirectUrl);
    }

    @Transactional
    public void activateAccount(String token, HttpServletResponse response) throws IOException {
        Optional<AccountActivationToken> optionalAccountActivationToken = 
            this.accountActivationTokenRepository.findByTokenValue(token);
        if (optionalAccountActivationToken.isEmpty()) {
            response.sendRedirect("https://auth.bookommerce.com:8282/page/login?account_activation_token_not_found");
            return;
        }
        
        AccountActivationToken accountActivationToken = optionalAccountActivationToken.get();
        if (accountActivationToken.getExpiresAt().isBefore(Instant.now())) {
            response.sendRedirect("https://auth.bookommerce.com:8282/page/login?account_activation_token_expired");
            return;
        }

        User user = accountActivationToken.getUser();
        user.setActivated(true);
        this.userRepository.save(user);
        this.accountActivationTokenRepository.delete(accountActivationToken);

        this.accountActivationSuccessEventPublisher.publishAccountActivationSuccessEvent(new AccountActivationSuccessEvent(user.getEmail()));
        response.sendRedirect("https://auth.bookommerce.com:8282/page/login?activation_success");
    }
}
