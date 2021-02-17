package com.gymer.components.common.mailing;

import com.gymer.components.common.entity.MailingDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Properties;
import java.util.Scanner;

@Component
public class MailingComponent {

    private final Environment environment;

    @Autowired
    public MailingComponent(Environment environment) {
        this.environment = environment;
    }

    public void sendEmail(MailingDetails details) {
        try {
            String mailBefore = readFileFromResourcesToString("src/main/resources/mailTemplate/mailBefore.html");
            String mailAfter = readFileFromResourcesToString("src/main/resources/mailTemplate/mailAfter.html");
            String username = environment.getProperty("mail.username");
            String password = environment.getProperty("mail.password");

            Properties prop = new Properties();
            prop.put("mail.smtp.auth", true);
            prop.put("mail.smtp.starttls.enable", "true");
            prop.put("mail.smtp.host", "smtp.gmail.com");
            prop.put("mail.smtp.port", "587");
            prop.put("mail.smtp.ssl.trust", "smtp.gmail.com");
            Session session = Session.getInstance(prop, new Authenticator() {
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(username, password);
                }
            });
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(username));
            message.setRecipients(
                    Message.RecipientType.TO, InternetAddress.parse(details.getMailTo()));
            message.setSubject(details.getSubject());

            MimeBodyPart mimeBodyPart = new MimeBodyPart();
            mimeBodyPart.setContent(mailBefore + details.getMessage() + mailAfter, "text/html");

            Multipart multipart = new MimeMultipart();
            multipart.addBodyPart(mimeBodyPart);

            message.setContent(multipart);

            Transport.send(message);
        } catch (FileNotFoundException e) {
            System.out.println("Read html files from resource folder unsuccessful");
        } catch (MessagingException e) {
            System.out.println("Can't send message or connect with mail service");
        }

    }

    private String readFileFromResourcesToString(String filename) throws FileNotFoundException {
        StringBuilder textFile = new StringBuilder();
        File file = new File(filename);
        InputStream is = new FileInputStream(file);

        try {
            Scanner scanner = new Scanner(is);
            while (scanner.hasNextLine()) {
                textFile.append(scanner.nextLine());
            }
        } catch (NullPointerException e) {
            System.out.println("File not found");
        }
        return textFile.toString();
    }

}
