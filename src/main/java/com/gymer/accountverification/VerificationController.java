package com.gymer.accountverification;

import com.gymer.commoncomponents.languagepack.LanguageComponent;
import com.gymer.commonresources.credential.entity.Credential;
import com.gymer.commonresources.credential.entity.Role;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@AllArgsConstructor
class VerificationController {

    private final VerificationService verificationService;
    private final LanguageComponent language;

    @GetMapping("/api/verify")
    public void verifyAccount(@RequestParam("code") String code) {
        Credential credential = verificationService.getCredentialByCode(code);
        if (verificationService.isUnverifiedUserNotExist(credential)
                || verificationService.isUnverifiedUserActivationCodeNotEqual(credential)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, language.invalidVerificationCode());
        }

        verificationService.changeActivationAttributesAndUpdateInDatabase(credential);
        if (credential.getRole()== Role.GUEST) {
            throw new ResponseStatusException(HttpStatus.OK, language.successfullyReservedNewSlot());
        } else {
            throw new ResponseStatusException(HttpStatus.OK, language.successfullyVerified());
        }
    }

}
