package org.dmdev.bookstore.security.verification;

import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;
    @Value("${spring.mail.username}")
    private String from;

    public Mono<Void> sendVerificationEmail(String email, String verificationToken) {
        String subject = "Verification Email";
        String path = "/api/verify";
        String message = "Click the button below to verify your email";
        return sendEmail(email, verificationToken, subject, path, message);
    }

    private Mono<Void> sendEmail(String email, String token, String subject, String path, String message) {
        return Mono.fromRunnable(() -> {
            try {
                String actionUrl = UriComponentsBuilder.fromUriString("http://localhost:8080")
                        .path(path)
                        .queryParam("token", token)
                        .toUriString();

                String content = """
                            <div style="font-family: Arial, sans-serif; max-width: 600px; margin: auto; padding: 20px; border-radius: 8px; background-color: #f9f9f9; text-align: center;">
                                <h2 style="color: #333;">%s</h2>
                                <p style="font-size: 16px; color: #555;">%s</p>
                                <a href="%s" style="display: inline-block; margin: 20px 0; padding: 10px 20px; font-size: 16px; color: #fff; background-color: #007bff; text-decoration: none; border-radius: 5px;">Proceed</a>
                                <p style="font-size: 14px; color: #777;">Or copy and paste this link into your browser:</p>
                                <p style="font-size: 14px; color: #007bff;">%s</p>
                                <p style="font-size: 12px; color: #aaa;">This is an automated message. Please do not reply.</p>
                            </div>
                        """.formatted(subject, message, actionUrl, actionUrl);

                MimeMessage mimeMessage = mailSender.createMimeMessage();
                MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);

                helper.setTo(email);
                helper.setSubject(subject);
                helper.setFrom(from);
                helper.setText(content, true);
                mailSender.send(mimeMessage);

            } catch (Exception e) {
                System.err.println("Failed to send email: " + e.getMessage());
            }
        });
    }

}
