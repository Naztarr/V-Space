package com.naz.vSpace.service.serviceImplementation;

import com.naz.vSpace.service.EmailService;
import com.naz.vSpace.util.EmailMessage;
import jakarta.mail.internet.MimeMessage;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class EmailImplementation implements EmailService {
    @Autowired
    private final JavaMailSender mailSender;

    public EmailImplementation(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }
    @Value("${EMAIL_USERNAME}")
    private String sender;

    /**
     * Sends an email message with the provided content, subject, and recipient.
     *
     * @param emailMessage   An object containing the receiver, subject and text.
     */
    @SneakyThrows
    @Override
    public void sendMail(EmailMessage emailMessage) {
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper messageHelper = new MimeMessageHelper(mimeMessage, true);
        messageHelper.setSubject(emailMessage.getSubject());
        messageHelper.setFrom(sender);
        messageHelper.setTo(emailMessage.getTo());
        messageHelper.setText(emailMessage.getText(), true);
        messageHelper.setSentDate(new Date(System.currentTimeMillis()));
        mailSender.send(mimeMessage);

    }
}
