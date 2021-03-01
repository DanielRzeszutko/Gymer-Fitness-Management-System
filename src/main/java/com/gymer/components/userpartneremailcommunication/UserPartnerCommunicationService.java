package com.gymer.components.userpartneremailcommunication;

import com.gymer.resources.partner.PartnerService;
import com.gymer.resources.partner.entity.Partner;
import com.gymer.resources.user.UserService;
import com.gymer.resources.user.entity.User;
import com.gymer.components.common.entity.JsonResponse;
import com.gymer.components.security.validation.AccountOwnerValidator;
import com.gymer.components.userpartneremailcommunication.entity.CommunicationDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserPartnerCommunicationService {

    private final PartnerService partnerService;
    private final UserService userService;
    private final AccountOwnerValidator accountOwnerValidator;
    private final MessageToPartnerService messageToPartnerService;

    @Autowired
    public UserPartnerCommunicationService(PartnerService partnerService,
                                           UserService userService,
                                           AccountOwnerValidator accountOwnerValidator,
                                           MessageToPartnerService messageToPartnerService) {
        this.partnerService = partnerService;
        this.userService = userService;
        this.accountOwnerValidator = accountOwnerValidator;
        this.messageToPartnerService = messageToPartnerService;
    }

    public JsonResponse sendMailToPartner(CommunicationDetails details, Long partnerId) {
        JsonResponse response = isUserSendingFromLoggedInAccount(details, partnerId);
        if (response.isError()) return response;

        Partner partner = partnerService.getElementById(partnerId);
        User user = userService.getElementById(details.getUserId());

        messageToPartnerService.sendMessageToPartner(partner, user, details.getMessage());
        return response;
    }

    private JsonResponse isUserSendingFromLoggedInAccount(CommunicationDetails details, Long partnerId) {
        if (!accountOwnerValidator.isOwnerLoggedIn(details.getUserId())) {
            return new JsonResponse("You don't have rights to send message.", true);
        }
        if (!partnerService.isElementExistById(details.getPartnerId())) {
            return new JsonResponse("Selected partner don't exists.", true);
        }
        if (!details.getPartnerId().equals(partnerId)) {
            return new JsonResponse("Conflict with partnerId in credentials or URL", true);
        }
        return new JsonResponse("Mail successfully send.", false);
    }

}
