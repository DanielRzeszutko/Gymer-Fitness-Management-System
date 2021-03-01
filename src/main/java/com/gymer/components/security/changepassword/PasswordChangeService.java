package com.gymer.components.security.changepassword;

import com.gymer.components.common.entity.JsonResponse;
import com.gymer.components.security.changepassword.entity.PasswordDetails;
import com.gymer.resources.credential.CredentialService;
import com.gymer.resources.credential.entity.Credential;
import com.gymer.resources.user.UserService;
import com.gymer.resources.user.entity.User;
import lombok.AllArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
class PasswordChangeService {

    private final PasswordEncoder passwordEncoder;
    private final UserService userService;
    private final CredentialService credentialService;

    public void changePassword(PasswordDetails passwordDetails, Credential credential) {
        String newPassword = passwordEncoder.encode(passwordDetails.getNewPassword());
        credential.setPassword(newPassword);
        credentialService.updateElement(credential);
    }

    public boolean isPasswordNotEqual(PasswordDetails passwordDetails, Credential credential) {
        return !passwordEncoder.matches(passwordDetails.getOldPassword(), credential.getPassword());
    }

    public Credential getUsersCredential(Long userId) {
        User user = userService.getElementById(userId);
        return credentialService.getElementById(user.getCredential().getId());
    }

}
