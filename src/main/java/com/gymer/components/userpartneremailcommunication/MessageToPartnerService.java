package com.gymer.components.userpartneremailcommunication;

import com.gymer.resources.partner.entity.Partner;
import com.gymer.resources.user.entity.User;
import com.gymer.components.common.entity.MailingDetails;
import com.gymer.components.common.mailing.EmailSender;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class MessageToPartnerService {

    private final EmailSender emailSender;

    public void sendMessageToPartner(Partner partner, User user, String message) {
        String emailTo = partner.getCredential().getEmail();
        String content = createContent(partner, user, message);
        String subject = createSubject();

        MailingDetails mailingDetails = new MailingDetails(emailTo, subject, content);
        emailSender.sendEmail(mailingDetails);

        MailingDetails copyOfMailingDetailsForUser = new MailingDetails(user.getCredential().getEmail(), subject, content);
        emailSender.sendEmail(copyOfMailingDetailsForUser);
    }

    private String createContent(Partner partner, User user, String message) {
        String userDetails = user.getFirstName() + " " + user.getLastName();
        String userEmail = user.getCredential().getEmail();
        String userPhone = user.getCredential().getPhoneNumber();

        return "Dear " + partner.getName() + ",<br>"
                + "You have new question from " + userDetails + "<br>"
                + message
                + "To contact user use below credentials:"
                + "Email: " + userEmail + " or phone number: " + userPhone
                + "Thank you for your support,<br>"
                + "Team Gymer.";
    }

    private String createSubject() {
        return "You have new question from user.";
    }

}
