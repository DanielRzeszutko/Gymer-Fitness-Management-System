package com.gymer.userpartnercommunication;

import com.gymer.common.crudresources.partner.PartnerService;
import com.gymer.common.crudresources.partner.entity.Partner;
import com.gymer.common.crudresources.user.UserService;
import com.gymer.common.crudresources.user.entity.User;
import com.gymer.common.entity.JsonResponse;
import com.gymer.security.validation.AccountOwnerValidator;
import com.gymer.userpartnercommunication.entity.CommunicationDetails;
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
    public JsonResponse sendMailToPartner(CommunicationDetails details, Long partnerId) {
        JsonResponse response = isUserSendingFromLoggedInAccount(details, partnerId);
        if (response.isResponseNotValid()) return response;

        Partner partner = partnerService.getElementById(partnerId);
        User user = userService.getElementById(details.getUserId());

        messageToPartnerService.sendMessageToPartner(partner, user, details.getMessage());
        return response;
    }

    private JsonResponse isUserSendingFromLoggedInAccount(CommunicationDetails details, Long partnerId) {
        if (isOwnerNotSendingMessage(details)) {
            return JsonResponse.invalidMessage("You don't have rights to send message.");
        }
        if (isElementNotExistInDatabase(details)) {
            return JsonResponse.invalidMessage("Selected partner don't exists.");
        }
        if (isConflictWithPartnerIdAndOwner(details, partnerId)) {
            return JsonResponse.invalidMessage("Conflict with partnerId in credentials or URL");
        }
        return JsonResponse.validMessage("Mail successfully send.");
    }

    private boolean isOwnerNotSendingMessage(CommunicationDetails details) {
        return !accountOwnerValidator.isOwnerLoggedIn(details.getUserId());
    }

    private boolean isElementNotExistInDatabase(CommunicationDetails details) {
        return !partnerService.isElementExistById(details.getPartnerId());
    }

    private boolean isConflictWithPartnerIdAndOwner(CommunicationDetails details, Long partnerId) {
        return !details.getPartnerId().equals(partnerId);
    }

}
