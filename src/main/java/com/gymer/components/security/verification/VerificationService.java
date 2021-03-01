package com.gymer.components.security.verification;

import com.gymer.resources.credential.CredentialService;
import com.gymer.resources.credential.entity.Credential;
import com.gymer.components.common.entity.JsonResponse;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
class VerificationService {

    private final CredentialService credentialService;

    /**
     * @param code - verification code from auto generated email with activation link
     * @return - Service method that returns response in JSON format and active account if code is correct.
     */

    public JsonResponse verify(String code) {
        Credential credential = credentialService.getCredentialByVerificationCode(code);
        if (credential == null) {
            return new JsonResponse("Sorry, account is already verified. Please login.", true);
        } else if (credential.isActivated()) {
            return new JsonResponse("Sorry, verification code is incorrect. Please try again", true);
        }
        credential.setVerificationCode(null);
        credential.setActivated(true);
        credentialService.updateElement(credential);
        return new JsonResponse("Account has been verified successfully", false);
    }

}
