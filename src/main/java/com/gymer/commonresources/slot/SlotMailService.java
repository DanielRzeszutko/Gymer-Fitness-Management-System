package com.gymer.commonresources.slot;

import com.gymer.commoncomponents.mailing.EmailSender;
import com.gymer.commoncomponents.mailing.MailingDetails;
import com.gymer.commonresources.partner.entity.Partner;
import com.gymer.commonresources.slot.entity.Slot;
import com.gymer.commonresources.user.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
class SlotMailService {

    private final Environment environment;
    private final EmailSender emailSender;

    @Autowired
    public SlotMailService(Environment environment, EmailSender emailSender) {
        this.environment = environment;
        this.emailSender = emailSender;
    }

    public void sendEmail(Partner partner, Slot slot) {
        List<User> usersSignedToSlot = slot.getUsers();

        String subject = createSubject();

        for (User user : usersSignedToSlot) {
            String emailTo = user.getCredential().getEmail();
            String message = createContent(partner, slot, user);
            MailingDetails mailingDetails = new MailingDetails(emailTo, subject, message);
            emailSender.sendEmail(mailingDetails);
        }
    }

    private String createContent(Partner partner, Slot slot, User user) {
        String slotDetails = slot.getSlotType() + " " + slot.getDate().toString() + " " + slot.getStartTime();
        String partnerUrl = environment.getProperty("server.address.frontend") + "/gymsite/" + partner.getId();
        String partnerName = partner.getName();

        return "Dear " + user.getFirstName() + " " + user.getLastName() + ",<br>"
                + "We are sorry but the partner deleted the slot for which you were reserved.<br>"
                + "Contact the slot owner to explain the problem.<br>"
                + "Slot details: " + slotDetails + "<br>"
                + "<h3><a href=\"" + partnerUrl + "\" target=\"_self\">\"" + partnerName + "\"</a></h3>"
                + "Thank you for your support,<br>"
                + "Team Gymer.";
    }

    private String createSubject() {
        return "Your slot was canceled.";
    }

}
