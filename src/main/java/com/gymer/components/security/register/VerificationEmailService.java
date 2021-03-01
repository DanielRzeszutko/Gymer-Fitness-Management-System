package com.gymer.components.security.register;

import com.gymer.resources.credential.entity.Credential;
import com.gymer.components.common.entity.MailingDetails;
import com.gymer.components.common.mailing.EmailSender;
import lombok.AllArgsConstructor;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
class VerificationEmailService {

    private final Environment environment;
    private final EmailSender emailSender;

    public void sendVerificationEmail(Credential credential) {
        String emailTo = credential.getEmail();
        String message = createContent(credential);
        String subject = createSubject();
        MailingDetails mailingDetails = new MailingDetails(emailTo, subject, message);
        emailSender.sendEmail(mailingDetails);
    }

    private String createContent(Credential credential) {
        String content = "Dear " + credential.getRole() + ",<br>"
                + "Please click the link below to verify your registration:<br>"
                + "<h3><a href=\"[[URL]]\" target=\"_self\">VERIFY</a></h3>"
                + "Thank you,<br>"
                + "Team Gymer.";

        String verifyURL = environment.getProperty("server.address.frontend") + "/verify?code=" + credential.getVerificationCode();
        return content.replace("[[URL]]", verifyURL);
    }

    private String createSubject() {
        return "Please verify your registration";
    }

}
