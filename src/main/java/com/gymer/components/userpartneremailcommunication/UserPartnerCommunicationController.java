package com.gymer.components.userpartneremailcommunication;

import com.gymer.components.common.entity.JsonResponse;
import com.gymer.components.userpartneremailcommunication.entity.CommunicationDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class UserPartnerCommunicationController {

    private final UserPartnerCommunicationService communicationService;

    @PostMapping("/api/partners/{partnerId}/message")
    public JsonResponse postMessageToPartner(@RequestBody CommunicationDetails details, @PathVariable Long partnerId) {
        return communicationService.sendMailToPartner(details, partnerId);
    }

}
