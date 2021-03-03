package com.gymer.accountverification;

import com.gymer.commonresources.credential.entity.Credential;
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

    @GetMapping("/api/verify")
    public void verifyAccount(@RequestParam("code") String code) {
        Credential credential = verificationService.getCredentialByCode(code);
        if (verificationService.isUnverifiedUserNotExist(credential)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Account is already verified. Please login.");
        }

        if (verificationService.isUnverifiedUserActivationCodeNotEqual(credential)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Sorry, verification code is incorrect. Please try again");
        }

        verificationService.changeActivationAttributesAndUpdateInDatabase(credential);
        throw new ResponseStatusException(HttpStatus.OK, "Account has been verified successfully");
    }

}
