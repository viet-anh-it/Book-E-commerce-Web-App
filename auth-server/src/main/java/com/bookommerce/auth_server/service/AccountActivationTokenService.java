package com.bookommerce.auth_server.service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindingResult;

import com.bookommerce.auth_server.entity.AccountActivationToken;
import com.bookommerce.auth_server.entity.User;
import com.bookommerce.auth_server.exception.EmailNotFoundException;
import com.bookommerce.auth_server.repository.AccountActivationTokenRepository;
import com.bookommerce.auth_server.repository.UserRepository;
import com.bookommerce.auth_server.validation.ValidationUtils;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

// @formatter:off
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AccountActivationTokenService {
    
    AccountActivationTokenRepository accountActivationTokenRepository;
    UserRepository userRepository;

    @Transactional
    public String createAccountActivationTokenForUser(String email) {
        AccountActivationToken accountActivationToken = new AccountActivationToken();
        String tokenValue = UUID.randomUUID().toString();
        accountActivationToken.setTokenValue(tokenValue);
        accountActivationToken.setExpiresAt(Instant.now().plus(24, ChronoUnit.HOURS));
        Optional<User> optionalUser = this.userRepository.findByEmail(email);
        if (optionalUser.isEmpty()) {
            BindingResult bindingResult = 
                ValidationUtils.createBindingResult(optionalUser, tokenValue, email, tokenValue);
            throw new EmailNotFoundException(bindingResult);
        }
        accountActivationToken.setUser(optionalUser.get());
        this.accountActivationTokenRepository.save(accountActivationToken);
        return tokenValue;
    }
}
