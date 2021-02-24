package com.gymer.components.security.register;

import com.gymer.api.address.entity.Address;
import com.gymer.api.credential.CredentialService;
import com.gymer.api.credential.entity.Credential;
import com.gymer.api.credential.entity.Role;
import com.gymer.api.partner.PartnerService;
import com.gymer.api.partner.entity.Partner;
import com.gymer.api.user.UserService;
import com.gymer.api.user.entity.User;
import com.gymer.components.common.entity.JsonResponse;
import com.gymer.components.security.register.entity.RegistrationDetails;
import net.bytebuddy.utility.RandomString;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.Collections;

@Service
class RegistrationService {

    private final PasswordEncoder passwordEncoder;
    private final UserService userService;
    private final PartnerService partnerService;
    private final CredentialService credentialService;
    private final VerificationEmailService emailService;

    @Autowired
    public RegistrationService(PasswordEncoder passwordEncoder, UserService userService,
                               PartnerService partnerService, CredentialService credentialService,
                               VerificationEmailService emailService) {
        this.passwordEncoder = passwordEncoder;
        this.userService = userService;
        this.partnerService = partnerService;
        this.credentialService = credentialService;
        this.emailService = emailService;
    }

    public JsonResponse registerAccount(RegistrationDetails details, Role role) {
        JsonResponse response = createJsonResponse(details);
        if (response.isError()) return response;

        if (credentialService.isCredentialExistsByEmail(details.getEmail())) {
            Credential existedCredential = credentialService.getCredentialByEmail(details.getEmail());
            updateCredential(details, existedCredential, role);
            emailService.sendVerificationEmail(existedCredential);
        } else {
            switch (role) {
                case USER -> registerUser(details);
                case PARTNER -> registerPartner(details);
            }
        }
        return response;
    }

    private void registerUser(RegistrationDetails details) {
        User user = createNewUser(details);
        userService.updateElement(user);
        emailService.sendVerificationEmail(user.getCredential());
    }

    private void registerPartner(RegistrationDetails details) {
        Partner partner = createNewPartner(details);
        partnerService.updateElement(partner);
        emailService.sendVerificationEmail(partner.getCredential());
    }

    private void updateCredential(RegistrationDetails details, Credential credential, Role role) {
        credential.setVerificationCode(RandomString.make(64));
        credential.setPassword(passwordEncoder.encode(details.getPassword()));
        credential.setRole(role);
        credentialService.updateElement(credential);
    }

    private User createNewUser(RegistrationDetails details) {
        Credential credential = createCredentialBy(details, Role.USER);
        return new User("", "", credential);
    }

    private Partner createNewPartner(RegistrationDetails details) {
        Credential credential = createCredentialBy(details, Role.PARTNER);
        Address address = new Address("", "", "", "");
        return new Partner("", "", "", "", "", credential, address,
                Collections.emptyList(), Collections.emptyList(), Collections.emptyList());
    }

    private JsonResponse createJsonResponse(RegistrationDetails userDetails) {
        if (userDetails.getEmail() == null || userDetails.getPassword() == null || userDetails.getConfirmPassword() == null) {
            return new JsonResponse("Invalid Json format. Should contain email password and confirmPassword", true);
        }
        if (userDetails.getEmail().equals("") || userDetails.getPassword().equals("") || userDetails.getConfirmPassword().equals("")) {
            return new JsonResponse("Fields cannot be empty!", false);
        }

        if (!userDetails.getPassword().equals(userDetails.getConfirmPassword())) {
            return new JsonResponse("Passwords do not match.", true);
        }
        if (credentialService.isActivatedCredentialExistsByEmail(userDetails.getEmail())) {
            return new JsonResponse("Account with this email already exists.", true);
        }
        return new JsonResponse("Registered successfully. Please check your email to verify your account", false);
    }

    private Credential createCredentialBy(RegistrationDetails userDetails, Role role) {
        String codedPassword = passwordEncoder.encode(userDetails.getPassword());
        Timestamp timestamp = new Timestamp(new java.util.Date().getTime());
        Credential credential = new Credential(userDetails.getEmail(), codedPassword, "", role, false, timestamp);
        credential.setVerificationCode(RandomString.make(64));
        return credential;
    }

}
