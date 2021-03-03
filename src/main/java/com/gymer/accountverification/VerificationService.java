package com.gymer.accountverification;

import com.gymer.common.resources.credential.CredentialService;
import com.gymer.common.resources.credential.entity.Credential;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
class VerificationService {

    private final CredentialService credentialService;

    public Credential getCredentialByCode(String code) {
        return credentialService.getCredentialByVerificationCode(code);
    }

    public boolean isUnverifiedUserNotExist(Credential credential) {
        return credential == null;
    }

    public boolean isUnverifiedUserActivationCodeNotEqual(Credential credential) {
        return credential.isActivated();
    }

    public void changeActivationAttributesAndUpdateInDatabase(Credential credential) {
        credential.setVerificationCode(null);
        credential.setActivated(true);
        credentialService.updateElement(credential);
    }

}
