package com.gymer.userpartnercommunication;

import com.gymer.commoncomponents.languagepack.LanguageComponent;
import com.gymer.commoncomponents.mailing.EmailSender;
import com.gymer.commoncomponents.mailing.MailingDetails;
import com.gymer.commonresources.partner.entity.Partner;
import com.gymer.commonresources.user.entity.User;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
class MessageToPartnerService {

    private final EmailSender emailSender;
    private final LanguageComponent language;

    /**
     * Method taking three parameters and using emailSender component to send message with email service.
     *
     * @param partner - entity containing business partner, addressee of mail.
     * @param user    - entity containing user sending message to the provided partner.
     * @param message - content of the message which will be send by email to the partner.
     */
    public void sendMessageToPartner(Partner partner, User user, String message) {
        String emailTo = partner.getCredential().getEmail();
        String content = language.getMessageFromUserToPartner(partner, user, message);
        String subject = language.getTitleFromUserToPartner();

        MailingDetails mailingDetails = new MailingDetails(emailTo, subject, content);
        emailSender.sendEmail(mailingDetails);

        MailingDetails copyOfMailingDetailsForUser = new MailingDetails(user.getCredential().getEmail(), subject, content);
        emailSender.sendEmail(copyOfMailingDetailsForUser);
    }

}
