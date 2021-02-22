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

    public JsonResponse registerUser(RegistrationDetails details, String siteURL) {
        JsonResponse response = createJsonResponse(details);
        if (response.isError()) return response;

        Credential credential = createCredentialBy(details, Role.USER);
        User user = new User("", "", credential);
        userService.updateElement(user);
        emailService.sendVerificationEmail(credential, siteURL);

        return response;
    }

    public JsonResponse registerPartner(RegistrationDetails details, String siteURL) {
        JsonResponse response = createJsonResponse(details);
        if (response.isError()) return response;

        Credential credential = createCredentialBy(details, Role.PARTNER);
        Address address = new Address("", "", "", "");
        Partner partner = new Partner("", "", "", "", "", credential, address,
                Collections.emptyList(), Collections.emptyList(), Collections.emptyList());
        partnerService.updateElement(partner);
        emailService.sendVerificationEmail(credential, siteURL);

        return response;
    }

    private JsonResponse createJsonResponse(RegistrationDetails userDetails) {
        if (userDetails.getEmail() == null || userDetails.getPassword() == null || userDetails.getConfirmPassword() == null) {
            return new JsonResponse("Invalid Json format. Should contain email password and confirmPassword", true);
        }
        if (!userDetails.getPassword().equals(userDetails.getConfirmPassword())) {
            return new JsonResponse("Passwords do not match.", true);
        }
        if (credentialService.isCredentialExistsByEmail(userDetails.getEmail())) {
            return new JsonResponse("Account with this email already exists.", true);
        }
        return new JsonResponse("Registered successfully.", false);
    }

    private Credential createCredentialBy(RegistrationDetails userDetails, Role role) {
        String codedPassword = passwordEncoder.encode(userDetails.getPassword());
        Timestamp timestamp = new Timestamp(new java.util.Date().getTime());
        Credential credential = new Credential(userDetails.getEmail(), codedPassword, "", role, false, timestamp);
        credential.setVerificationCode(RandomString.make(64));
        return credential;
    }

}
