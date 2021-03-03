package com.gymer.accountregistration;

import com.gymer.common.resources.credential.entity.Role;
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
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Fields cannot be empty!");
        }

        if (service.isPasswordAndConfirmPasswordNotEqual(registrationDetails)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Passwords do not match.");
        }

        if (service.isAccountAlreadyExists(registrationDetails)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Account with this email already exists.");
        }

        if (service.isUserAlreadyExists(registrationDetails.getEmail())) {
            service.ifNotActivatedUserWithThisEmailExists(registrationDetails, role);
            throw new ResponseStatusException(HttpStatus.OK, "Activation email resend. Please check your email to verify your account.");
        }

        service.registerNewAccountWithSpecificRole(registrationDetails, role);
        throw new ResponseStatusException(HttpStatus.OK, "Registered successfully. Please check your email to verify your account.");
    }

}
