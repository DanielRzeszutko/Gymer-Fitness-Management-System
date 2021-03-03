package com.gymer.userpartnercommunication;

import com.gymer.commoncomponents.accountvalidator.AccountOwnerValidator;
import com.gymer.commonresources.partner.PartnerService;
import com.gymer.commonresources.partner.entity.Partner;
import com.gymer.commonresources.user.UserService;
import com.gymer.commonresources.user.entity.User;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
class UserPartnerCommunicationService {

    private final PartnerService partnerService;
    private final UserService userService;
    private final AccountOwnerValidator accountOwnerValidator;
    private final MessageToPartnerService messageToPartnerService;

    /**
     * Method receiving all objects from controller, like CommunicationDetails and partnerId.
     * First of all response is build based on needed checks. Second if there is valid response the
     * rest of method is launched, like creating message with detailed service and sending to the partner.
     *
     * @param details   - object containing three fields, partnerId - addressee of a mail, userId - sender
     *                  and message in text format.
     * @param partnerId - addressee ID, must be equal to the partnerID provided in details object.
     * @return JsonResponse - object with message and valid status if data is filled successfully or
     * message and invalid status if any error occurs during reading the text files.
     */
    public void sendMailToPartner(CommunicationDetails details, Long partnerId) {
        Partner partner = partnerService.getElementById(partnerId);
        User user = userService.getElementById(details.getUserId());
        messageToPartnerService.sendMessageToPartner(partner, user, details.getMessage());
    }

    public boolean isOwnerNotSendingMessage(CommunicationDetails details) {
        return !accountOwnerValidator.isOwnerLoggedIn(details.getUserId());
    }

    public boolean isElementNotExistInDatabase(CommunicationDetails details) {
        return !partnerService.isElementExistById(details.getPartnerId());
    }

    public boolean isConflictWithPartnerIdAndOwner(CommunicationDetails details, Long partnerId) {
        return !details.getPartnerId().equals(partnerId);
    }

}
