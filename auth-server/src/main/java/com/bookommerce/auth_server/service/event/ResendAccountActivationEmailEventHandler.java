package com.bookommerce.auth_server.service.event;

import org.springframework.data.redis.connection.stream.MapRecord;
import org.springframework.data.redis.stream.StreamListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bookommerce.auth_server.dto.event.ResendAccountActivationEmailEvent;
import com.bookommerce.auth_server.service.AccountActivationTokenService;
import com.bookommerce.auth_server.service.MailService;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

//@formatter:off
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ResendAccountActivationEmailEventHandler implements StreamListener<String, MapRecord<String, String, String>> {
    
    ObjectMapper objectMapper;
    static String AUTH_SERVER_BASE_URL = "https://auth.bookommerce.com:8282";
    static String ACCOUNT_ACTIVATION_API_PATH = "/api/account/activate";
    MailService mailService;
    AccountActivationTokenService accountActivationTokenService;
    
    @Override
    @Transactional
    public void onMessage(MapRecord<String, String, String> message) {
        ResendAccountActivationEmailEvent event = 
            this.objectMapper.convertValue(message.getValue(), ResendAccountActivationEmailEvent.class);
        String tokenValue = this.accountActivationTokenService.createAccountActivationTokenForUser(event.getEmail());
        String accountActivationUrl = AUTH_SERVER_BASE_URL + ACCOUNT_ACTIVATION_API_PATH + "?token=" + tokenValue;
        this.mailService.sendAccountActivationEmail(event.getEmail(), accountActivationUrl);
    }
}
