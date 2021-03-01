package com.gymer.security.verification;

import com.gymer.resources.credential.CredentialService;
import com.gymer.resources.credential.entity.Credential;
import com.gymer.common.entity.JsonResponse;
import lombok.AllArgsConstructor;
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

        if (isUnverifiedUserNotExist(credential)) {
            return JsonResponse.invalidMessage("Sorry, account is already verified. Please login.");
        }

        if (isUnverifiedUserActivationCodeNotEqual(credential)) {
            return JsonResponse.invalidMessage("Sorry, verification code is incorrect. Please try again");
        }

        changeActivationAttributesAndUpdateInDatabase(credential);
        return JsonResponse.validMessage("Account has been verified successfully");
    }

    private boolean isUnverifiedUserNotExist(Credential credential) {
        return credential == null;
    }

    private boolean isUnverifiedUserActivationCodeNotEqual(Credential credential) {
        return credential.isActivated();
    }

    private void changeActivationAttributesAndUpdateInDatabase(Credential credential) {
        credential.setVerificationCode(null);
        credential.setActivated(true);
        credentialService.updateElement(credential);
    }

}
