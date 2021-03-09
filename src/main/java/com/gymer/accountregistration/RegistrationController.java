package com.gymer.accountregistration;

import com.gymer.commoncomponents.languagepack.LanguageComponent;
import com.gymer.commonresources.credential.entity.Role;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@AllArgsConstructor
class RegistrationController {

    private final RegistrationService service;
    private final LanguageComponent language;

    @PostMapping("/api/registration/user")
    public void registerUser(@RequestBody RegistrationDetails registrationDetails) {
        registerAccount(registrationDetails, Role.USER);
    }

    @PostMapping("/api/registration/partner")
    public void registerPartner(@RequestBody RegistrationDetails registrationDetails) {
        registerAccount(registrationDetails, Role.PARTNER);
    }

    private void registerAccount(RegistrationDetails registrationDetails, Role role) {
        if (service.isAnyFieldBlankOrEmpty(registrationDetails)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, language.fieldsCannotBeEmpty());
        }

        if (service.isPasswordAndConfirmPasswordNotEqual(registrationDetails)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, language.passwordsDoesntEqual());
        }

        if (service.isAccountAlreadyExists(registrationDetails)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, language.accountAlreadyExists());
        }

        if (service.isUserAlreadyExists(registrationDetails.getEmail())) {
            service.ifNotActivatedUserWithThisEmailExists(registrationDetails, role);
            throw new ResponseStatusException(HttpStatus.OK, language.activationMailSend());
        }

        service.registerNewAccountWithSpecificRole(registrationDetails, role);
        throw new ResponseStatusException(HttpStatus.OK, language.registeredSuccessfully());
    }

}
