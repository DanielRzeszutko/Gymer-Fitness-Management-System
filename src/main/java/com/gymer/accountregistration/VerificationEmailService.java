package com.gymer.accountregistration;

import com.gymer.commoncomponents.languagepack.LanguageComponent;
import com.gymer.commoncomponents.mailing.EmailSender;
import com.gymer.commoncomponents.mailing.MailingDetails;
import com.gymer.commonresources.credential.entity.Credential;
import lombok.AllArgsConstructor;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
class VerificationEmailService {

    private final Environment environment;
    private final EmailSender emailSender;
    private final LanguageComponent language;

    public void sendVerificationEmail(Credential credential) {
        String emailTo = credential.getEmail();
        String verifyURL = environment.getProperty("server.address.frontend") + "/verify?code=" + credential.getVerificationCode();
        String message = language.getVerificationEmail(credential, verifyURL);
        String subject = language.getVerificationTitle();
        MailingDetails mailingDetails = new MailingDetails(emailTo, subject, message);
        emailSender.sendEmail(mailingDetails);
    }

}
