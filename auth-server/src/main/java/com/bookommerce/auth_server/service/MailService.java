package com.bookommerce.auth_server.service;

import java.nio.charset.StandardCharsets;

import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import jakarta.mail.internet.MimeMessage;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

// @formatter:off
@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class MailService {
    
    JavaMailSender javaMailSender;
    TemplateEngine templateEngine;

    public void sendAccountActivationEmail(String to, String accountActivationUrl) {
        try {
            Context context = new Context();
            context.setVariable("accountActivationUrl", accountActivationUrl);
            
            MimeMessage mimeMessage = this.javaMailSender.createMimeMessage();
            MimeMessageHelper message = new MimeMessageHelper(mimeMessage, true, StandardCharsets.UTF_8.name());
            String content = this.templateEngine.process("registration-success-email", context);

            message.setFrom("noreply@bookommerce.com");
            message.setTo(to);
            message.setSubject("Email kích hoạt tài khoản từ Bookommerce");
            message.setText(content, true);

            this.javaMailSender.send(message.getMimeMessage());
        } catch (Exception exception) {
            log.error("Exception while sending account activation email to: {}", to, exception);
        }
    }
}
