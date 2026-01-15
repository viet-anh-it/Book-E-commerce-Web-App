package com.bookommerce.auth_server.service.event;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

import org.springframework.data.redis.connection.stream.MapRecord;
import org.springframework.data.redis.stream.StreamListener;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import com.bookommerce.auth_server.dto.event.RegistrationSuccessEvent;
import com.bookommerce.auth_server.entity.AccountActivationToken;
import com.bookommerce.auth_server.repository.AccountActivationTokenRepository;
import com.bookommerce.auth_server.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.mail.internet.MimeMessage;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

//@formatter:off
@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class RegistrationSuccessEventListener implements StreamListener<String, MapRecord<String, String, String>> {

    static String AUTH_SERVER_BASE_URL = "https://auth.bookommerce.com:8282";
    static String ACCOUNT_ACTIVATION_API_PATH = "/api/account/activate";
    JavaMailSender javaMailSender;
    TemplateEngine templateEngine;
    ObjectMapper objectMapper = new ObjectMapper();
    AccountActivationTokenRepository accountActivationTokenRepository;
    UserRepository userRepository;

    @Override
    public void onMessage(MapRecord<String, String, String> message) {
        RegistrationSuccessEvent event =
            objectMapper.convertValue(message.getValue(), RegistrationSuccessEvent.class);
        AccountActivationToken accountActivationToken = new AccountActivationToken();
        String tokenValue = UUID.randomUUID().toString();
        String accountActivationUrl = AUTH_SERVER_BASE_URL + ACCOUNT_ACTIVATION_API_PATH + "?token=" + tokenValue;
        accountActivationToken.setTokenValue(tokenValue);
        accountActivationToken.setExpiresAt(Instant.now().plus(24, ChronoUnit.HOURS));
        accountActivationToken.setUser(this.userRepository.findByEmail(event.getEmail()).get());
        this.accountActivationTokenRepository.save(accountActivationToken);
        this.send(event.getEmail(), accountActivationUrl);
        log.info(">>>>>>>>>> Email sent to: {}", event.getEmail());
    }

    private void send(String to, String accountActivationUrl) {
        try {
            Context context = new Context();
            context.setVariable("accountActivationUrl", accountActivationUrl);
            
            MimeMessage mimeMessage = this.javaMailSender.createMimeMessage();
            MimeMessageHelper message = new MimeMessageHelper(mimeMessage, true, StandardCharsets.UTF_8.name());
            String content = this.templateEngine.process("registration-success-email", context);

            message.setFrom("noreply@bookommerce.com");
            message.setTo(to);
            message.setSubject("Welcome to Bookommerce");
            message.setText(content, true);

            this.javaMailSender.send(message.getMimeMessage());
        } catch (Exception exception) {
            log.error("Failed to send email", exception);
        }
    }
}
