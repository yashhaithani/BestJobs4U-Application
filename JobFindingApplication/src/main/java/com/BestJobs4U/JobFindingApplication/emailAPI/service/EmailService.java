package com.BestJobs4U.JobFindingApplication.emailAPI.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    public void sendEmailWithAttachments(String toEmail, String subject, String body, String imagePath, String pdfPath) throws MessagingException {
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);

        helper.setFrom("email.address.for.development@gmail.com");
        helper.setTo(toEmail);
        helper.setSubject(subject);
        helper.setText(body, true); // 'true' means the email body is HTML

        // Attach image
        Resource imageResource = new ClassPathResource("static/images/emailSignature.png");
        helper.addAttachment(imageResource.getFilename(), imageResource);

        // Attach PDF
        Resource pdfResource = new ClassPathResource("static/pdfs/interviewDetails.pdf");
        helper.addAttachment(pdfResource.getFilename(), pdfResource);

        mailSender.send(mimeMessage);
    }
}
