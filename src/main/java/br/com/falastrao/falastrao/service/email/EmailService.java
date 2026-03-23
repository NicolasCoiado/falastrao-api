package br.com.falastrao.falastrao.service.email;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private final JavaMailSender mailSender;

    @Value("${app.base-url}")
    private String baseUrl;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendVerificationEmail(String email, String token) {

        String link = baseUrl + "/auth/verify?token=" + token;

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);
        message.setSubject("Verify your account");
        message.setText("Click the link to verify your account:\n\n" + link);

        mailSender.send(message);
    }

    public void sendPasswordResetEmail(String email, String token) {
        String link = baseUrl + "/auth/reset-password?token=" + token;

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);
        message.setSubject("Reset your password");
        message.setText("Click the link to reset your password:\n\n" + link +
                "\n\nThis link expires in 1 hour.");

        mailSender.send(message);
    }

}