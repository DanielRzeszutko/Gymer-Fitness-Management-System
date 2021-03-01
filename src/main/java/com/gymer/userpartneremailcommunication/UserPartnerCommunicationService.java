package com.gymer.userpartneremailcommunication;

import com.gymer.common.entity.JsonResponse;
import com.gymer.resources.partner.PartnerService;
import com.gymer.resources.partner.entity.Partner;
import com.gymer.resources.user.UserService;
import com.gymer.resources.user.entity.User;
import com.gymer.security.validation.AccountOwnerValidator;
import com.gymer.userpartneremailcommunication.entity.CommunicationDetails;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
class UserPartnerCommunicationService {

    private final PartnerService partnerService;
    private final UserService userService;
    private final AccountOwnerValidator accountOwnerValidator;
    private final MessageToPartnerService messageToPartnerService;

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
