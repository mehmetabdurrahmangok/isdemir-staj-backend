package com.isdemirstaj.backend.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private final JavaMailSender mailSender;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendExcelEmail(String to, String subject, String body, byte[] excelData, String attachmentName) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            // true parametresi, bu e-postanın bir dosya eki (attachment) içerdiğini belirtir
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(body);
            helper.addAttachment(attachmentName, new ByteArrayResource(excelData));

            mailSender.send(message);
            System.out.println("✅ Excel Raporu başarıyla şu adrese e-postalandı: " + to);

        } catch (MessagingException e) {
            System.err.println("❌ E-posta gönderilirken hata oluştu: " + e.getMessage());
        }
    }
}