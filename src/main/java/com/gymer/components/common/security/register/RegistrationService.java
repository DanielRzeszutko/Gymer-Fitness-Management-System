package com.gymer.components.common.security.register;

import com.gymer.api.address.entity.Address;
import com.gymer.api.credential.CredentialService;
import com.gymer.api.credential.entity.Credential;
import com.gymer.api.credential.entity.Role;
import com.gymer.api.partner.PartnerService;
import com.gymer.api.partner.entity.Partner;
import com.gymer.api.user.UserService;
import com.gymer.api.user.entity.User;
import com.gymer.components.common.entity.JsonResponse;
import com.gymer.components.common.security.register.entity.RegistrationDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.Collections;

@Service
public class RegistrationService {

    private final PasswordEncoder passwordEncoder;
    private final UserService userService;
    private final PartnerService partnerService;
    private final CredentialService credentialService;

    @Autowired
    public RegistrationService(PasswordEncoder passwordEncoder, UserService userService,
                               PartnerService partnerService, CredentialService credentialService) {
        this.passwordEncoder = passwordEncoder;
        this.userService = userService;
        this.partnerService = partnerService;
        this.credentialService = credentialService;
    }

    public JsonResponse registerUser(RegistrationDetails details) {
        JsonResponse response = createJsonResponse(details);
        if (response.isError()) return response;

        Credential credential = createCredentialBy(details, Role.USER);
        User user = new User("", "", credential);
        userService.updateElement(user);
        return response;
    }

    public JsonResponse registerPartner(RegistrationDetails details) {
        JsonResponse response = createJsonResponse(details);
        if (response.isError()) return response;

        Credential credential = createCredentialBy(details, Role.PARTNER);
        Address address = new Address("", "", "", "");
        Partner partner = new Partner("", "", "", "", "", credential, address,
                Collections.emptyList(), Collections.emptyList(), Collections.emptyList());
        partnerService.updateElement(partner);
        return response;
    }

    private JsonResponse createJsonResponse(RegistrationDetails userDetails) {
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
        return new Credential(userDetails.getEmail(),
                codedPassword, "", role, true, false, timestamp);
    }

}
