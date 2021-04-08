package com.gymer.slotsreservation;

import com.gymer.commoncomponents.languagepack.LanguageComponent;
import com.gymer.commoncomponents.mailing.EmailSender;
import com.gymer.commoncomponents.mailing.MailingDetails;
import com.gymer.commonresources.credential.entity.Credential;
import com.gymer.commonresources.slot.entity.Slot;
import lombok.AllArgsConstructor;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
class GuestVerifyEmailService {

    private final Environment environment;
    private final EmailSender emailSender;
    private final LanguageComponent language;


    public void sendGuestVerificationEmail(Credential credential, Slot slot) {
        String emailTo = credential.getEmail();
        String verifyURL = environment.getProperty("server.address.frontend") + "/verify?code=" + credential.getVerificationCode();
        String message = language.getGuestVerificationEmail(credential, verifyURL, slot);
        String subject = language.getGuestVerificationTitle(slot);
        MailingDetails mailingDetails = new MailingDetails(emailTo, subject, message);
        emailSender.sendEmail(mailingDetails);
    }

}