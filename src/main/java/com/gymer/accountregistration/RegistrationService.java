package com.gymer.accountregistration;

import com.gymer.common.resources.address.entity.Address;
import com.gymer.common.resources.credential.CredentialService;
import com.gymer.common.resources.credential.entity.Credential;
import com.gymer.common.resources.credential.entity.Role;
import com.gymer.common.resources.partner.PartnerService;
import com.gymer.common.resources.partner.entity.Partner;
import com.gymer.common.resources.user.UserService;
import com.gymer.common.resources.user.entity.User;
import lombok.AllArgsConstructor;
import net.bytebuddy.utility.RandomString;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.Collections;

@Service
@AllArgsConstructor
class RegistrationService {

    private final PasswordEncoder passwordEncoder;
    private final UserService userService;
    private final PartnerService partnerService;
    private final CredentialService credentialService;
    private final VerificationEmailService emailService;

    public boolean isAnyFieldBlankOrEmpty(RegistrationDetails userDetails) {
        return isEmailBlankOrEmpty(userDetails.getEmail())
                || isPasswordBlankOrEmpty(userDetails.getPassword())
                || isConfirmPasswordBlankOrEmpty(userDetails.getConfirmPassword());
    }

    private boolean isEmailBlankOrEmpty(String email) {
        return email == null || email.equals("");
    }

    private boolean isPasswordBlankOrEmpty(String password) {
        return password == null || password.equals("");
    }

    private boolean isConfirmPasswordBlankOrEmpty(String confirmPassword) {
        return confirmPassword == null || confirmPassword.equals("");
    }

    public boolean isPasswordAndConfirmPasswordNotEqual(RegistrationDetails userDetails) {
        return !userDetails.getPassword().equals(userDetails.getConfirmPassword());
    }

    public boolean isAccountAlreadyExists(RegistrationDetails userDetails) {
        return credentialService.isActivatedCredentialExistsByEmail(userDetails.getEmail());
    }

    public boolean isUserAlreadyExists(String email) {
        return credentialService.isCredentialExistsByEmail(email);
    }

    public void ifNotActivatedUserWithThisEmailExists(RegistrationDetails details, Role role) {
        Credential existedCredential = credentialService.getCredentialByEmail(details.getEmail());
        updateCredential(details, existedCredential, role);
        emailService.sendVerificationEmail(existedCredential);
    }

    public void registerNewAccountWithSpecificRole(RegistrationDetails details, Role role) {
        switch (role) {
            case USER -> registerUser(details);
            case PARTNER -> registerPartner(details);
        }
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

    private Credential createCredentialBy(RegistrationDetails userDetails, Role role) {
        String codedPassword = passwordEncoder.encode(userDetails.getPassword());
        Timestamp timestamp = new Timestamp(new java.util.Date().getTime());
        Credential credential = new Credential(userDetails.getEmail(), codedPassword, "", role, false, timestamp);
        credential.setVerificationCode(RandomString.make(64));
        return credential;
    }

}
