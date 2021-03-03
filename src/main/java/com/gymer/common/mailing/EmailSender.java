package com.gymer.common.mailing;

import lombok.AllArgsConstructor;
import org.springframework.core.env.Environment;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.*;
import java.util.Objects;
import java.util.Scanner;

/**
 * One class mail system - component responsible for connecting with external email server.
 * Provides functionality of adding HTML body to the mail from predefined resources.
 */
@Component
@AllArgsConstructor
public class EmailSender {

    private final Environment environment;
    private final JavaMailSender javaMailSender;

    /**
     * Main method that sends emails - starts with reading template files from the resources folder.
     * After building template method connects to the external email service provider and establish
     * connection. Next is sending email to the inputted address with inputted message.
     *
     * @param mailingDetails - Object providing three fields: emailTo, subject and emailContent.
     */
    public void sendEmail(MailingDetails mailingDetails) {
        try {
            String addressFrom = Objects.requireNonNull(environment.getProperty("spring.mail.username"));
            String content = getMailContent(mailingDetails);

            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message);

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

    private String getMailContent(MailingDetails mailingDetails) throws FileNotFoundException {
        String mailBefore = readFileFromResourcesToString("src/main/resources/mailTemplate/mailBefore.html");
        String mailAfter = readFileFromResourcesToString("src/main/resources/mailTemplate/mailAfter.html");
        return mailBefore + mailingDetails.getMessage() + mailAfter;
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
