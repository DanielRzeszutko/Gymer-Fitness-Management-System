package com.gymer.userpartnercommunication;

import com.gymer.common.entity.JsonResponse;
import com.gymer.userpartnercommunication.entity.CommunicationDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
class UserPartnerCommunicationController {

    private final UserPartnerCommunicationService communicationService;

    /**
     * Controller endpoint receiving CommunicationDetails object in body. Checking all rights to
     * sending emails from valid account and to valid partner message is send by service class.
     * @param details - object containing three fields, partnerId - addressee of a mail, userId - sender
     *                and message in text format.
     * @param partnerId - addressee ID, must be equal to the partnerID provided in details object.
     * @return JsonResponse - object with message and valid status if data is filled successfully or
     * message and invalid status if any error occurs during reading the text files.
     */
    @PostMapping("/api/partners/{partnerId}/message")
    public JsonResponse postMessageToPartner(@RequestBody CommunicationDetails details, @PathVariable Long partnerId) {
        return communicationService.sendMailToPartner(details, partnerId);
    }

}
