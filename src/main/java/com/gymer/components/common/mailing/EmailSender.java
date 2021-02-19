package com.gymer.components.common.mailing;

import com.gymer.components.common.entity.MailingDetails;
import org.springframework.core.env.Environment;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.*;
import java.util.Scanner;

@Component
public class EmailSender {

    private final Environment environment;
    private final JavaMailSender javaMailSender;

    public EmailSender(Environment environment, JavaMailSender mailSender) {
        this.environment = environment;
        this.javaMailSender = mailSender;
    }

    public void sendEmail(MailingDetails mailingDetails) {
        try {
            String mailBefore = readFileFromResourcesToString("src/main/resources/mailTemplate/mailBefore.html");
            String mailAfter = readFileFromResourcesToString("src/main/resources/mailTemplate/mailAfter.html");
            String addressFrom = environment.getProperty("spring.mail.username");
            String content = mailBefore + mailingDetails.getMessage() + mailAfter;

            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message);

            assert addressFrom != null;
            helper.setFrom(addressFrom, "Team Gymer");
            helper.setTo(mailingDetails.getMailTo());
            helper.setSubject(mailingDetails.getSubject());

            helper.setText(content, true);
            javaMailSender.send(message);

        } catch (FileNotFoundException e) {
            System.out.println("Read html files from resource folder unsuccessful");
        } catch (
                MessagingException e) {
            System.out.println("Can't send message or connect with mail service");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
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
