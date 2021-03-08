package com.gymer.mailandsmsnotifications;

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

    public void sendNotification(User user, Slot slot) {
        String emailTo = user.getCredential().getEmail();
        String content = createContent(user, slot);
        String subject = createSubject();

        MailingDetails mailingDetails = new MailingDetails(emailTo, subject, content);
        emailSender.sendEmail(mailingDetails);

        MailingDetails copyOfMailingDetailsForUser = new MailingDetails(user.getCredential().getEmail(), subject, content);
        emailSender.sendEmail(copyOfMailingDetailsForUser);
    }

    private String createContent(User user, Slot slot) {
        String userDetails = user.getFirstName() + " " + user.getLastName();

        return "Dear " + userDetails + ",<br>"
                + "Your slot " + slot.getSlotType() + "starting in an hour.<br>"
                + "Details: " + slot.getDescription() + "<br>"
                + "Thank you for your support,<br>"
                + "Team Gymer.";
    }

    private String createSubject() {
        return "Your slot starting in an hour.";
    }

}
