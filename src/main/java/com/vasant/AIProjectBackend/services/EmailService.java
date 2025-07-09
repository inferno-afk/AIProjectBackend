package com.vasant.AIProjectBackend.services;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender javaMailSender;

    @Value("${spring.mail.properties.mail.smtp.from}")
    private String fromEmail;
    private final TemplateEngine templateEngine;

    public void sendWelcomeEmail(String toEmail, String name){
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromEmail);
        message.setTo(toEmail);
        message.setSubject("Welcome to Our Platform!");
        message.setText("Hello " + name + ",\n\n" +
                "Thank you for signing up! We are excited to have you on board.\n\n" +
                "Best regards,\n" +
                "The Team");

        javaMailSender.send(message);
    }

    public void sendResetOtpEmail(String toEmail, String otp, String username) throws MessagingException {
        Context context = new Context();
        context.setVariable("otp", otp);
        context.setVariable("username", username);

        String htmlContent = templateEngine.process("password-reset-email", context);

        sendHtmlEmail(toEmail, "Reset Your Password", htmlContent);
    }

    public void sendOtpEmail(String toEmail, String otp, String username) throws MessagingException {
        Context context = new Context();
        context.setVariable("otp", otp);
        context.setVariable("username", username);

        String htmlContent = templateEngine.process("verify-email", context);

        sendHtmlEmail(toEmail, "Verify Your Email", htmlContent);
    }

    private void sendHtmlEmail(String toEmail, String subject, String htmlContent) throws MessagingException {
        MimeMessage message = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED, "UTF-8");

        helper.setFrom(fromEmail);
        helper.setTo(toEmail);
        helper.setSubject(subject);
        helper.setText(htmlContent, true); // true = is HTML

        javaMailSender.send(message);
    }
}
