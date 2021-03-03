package com.gymer.userpartnercommunication;

import com.gymer.userpartnercommunication.entity.CommunicationDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequiredArgsConstructor
class UserPartnerCommunicationController {

    private final UserPartnerCommunicationService communicationService;

    /**
     * Controller endpoint receiving CommunicationDetails object in body. Checking all rights to
     * sending emails from valid account and to valid partner message is send by service class.
     *
     * @param details   - object containing three fields, partnerId - addressee of a mail, userId - sender
     *                  and message in text format.
     * @param partnerId - addressee ID, must be equal to the partnerID provided in details object.
     * @return JsonResponse - object with message and valid status if data is filled successfully or
     * message and invalid status if any error occurs during reading the text files.
     */
    @PostMapping("/api/partners/{partnerId}/message")
    public void postMessageToPartner(@RequestBody CommunicationDetails details, @PathVariable Long partnerId) {
        if (communicationService.isOwnerNotSendingMessage(details)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "You don't have rights to send message.");
        }
        if (communicationService.isElementNotExistInDatabase(details)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Selected partner don't exists.");
        }
        if (communicationService.isConflictWithPartnerIdAndOwner(details, partnerId)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Conflict with partnerId in credentials or URL");
        }
        communicationService.sendMailToPartner(details, partnerId);
        throw new ResponseStatusException(HttpStatus.OK, "Mail successfully send.");
    }

}
