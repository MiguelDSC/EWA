package app.repositories;

/**
 * This method <Description of functionality
 *
 * @author R. Siepelinga
 */

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailSenderRepository {

    @Autowired
    private JavaMailSender mailSender;

    public void sendEmail(String toEmail, String body,
                          String subject) {
        SimpleMailMessage message = new SimpleMailMessage();

        message.setFrom("noreply_team1@ewa.com");
        message.setTo(toEmail);
        message.setText(body);
        message.setSubject(subject);

        mailSender.send(message);
    }
}