package com.bookommerce.auth_server.service;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.util.Map;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextHolderStrategy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.stereotype.Service;
import org.springframework.validation.BeanPropertyBindingResult;

import com.bookommerce.auth_server.dto.request.LoginRequestDto;
import com.bookommerce.auth_server.dto.request.RegistrationRequestDto;
import com.bookommerce.auth_server.entity.User;
import com.bookommerce.auth_server.exception.EmailAlreadyExistedException;
import com.bookommerce.auth_server.mapper.UserMapper;
import com.bookommerce.auth_server.repository.UserRepository;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AuthService {

    UserRepository userRepository;
    UserMapper userMapper;
    PasswordEncoder passwordEncoder;
    AuthenticationManager authenticationManager;
    SecurityContextRepository securityContextRepository;

    //@formatter:off
    public void register(RegistrationRequestDto registrationRequestDto) {
        if (this.userRepository.findByEmail(registrationRequestDto.email()).isPresent()) {
            BeanPropertyBindingResult bindingResult = new BeanPropertyBindingResult(registrationRequestDto, "registrationRequestDto");
            bindingResult.rejectValue("email", "email", "3:Email already existed");
            throw new EmailAlreadyExistedException(bindingResult);
        }
        User user = this.userMapper.toUser(registrationRequestDto);
        user.setPassword(this.passwordEncoder.encode(registrationRequestDto.password()));
        this.userRepository.save(user);
    }

    //@formatter:off
    public Map<String, String> login(LoginRequestDto loginRequestDto, HttpServletRequest request, HttpServletResponse response) {
        UsernamePasswordAuthenticationToken authenticationToken = 
            new UsernamePasswordAuthenticationToken(loginRequestDto.email(), loginRequestDto.password());
        Authentication authentication = this.authenticationManager.authenticate(authenticationToken);
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
        String redirectUrl = "https://bff.bookommerce.com:8181/oauth2/authorization/bff";
        return Map.of("redirect", redirectUrl);
    }
}
