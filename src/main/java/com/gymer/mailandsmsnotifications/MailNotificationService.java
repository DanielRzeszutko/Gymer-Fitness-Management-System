package com.gymer.mailandsmsnotifications;

import com.gymer.commoncomponents.languagepack.LanguageComponent;
import com.gymer.commoncomponents.mailing.EmailSender;
import com.gymer.commoncomponents.mailing.MailingDetails;
import com.gymer.commonresources.slot.entity.Slot;
import com.gymer.commonresources.user.entity.User;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
class MailNotificationService {

    private final EmailSender emailSender;
    private final LanguageComponent language;

    public void sendNotification(User user, Slot slot) {
        String emailTo = user.getCredential().getEmail();
        String content = language.getMailNotificationWhenSlotStartsInAnHour(user, slot);
        String subject = language.getSmsTitleWhenSlotStartsInAnHour();

        MailingDetails mailingDetails = new MailingDetails(emailTo, subject, content);
        emailSender.sendEmail(mailingDetails);

        MailingDetails copyOfMailingDetailsForUser = new MailingDetails(user.getCredential().getEmail(), subject, content);
        emailSender.sendEmail(copyOfMailingDetailsForUser);
    }

}
