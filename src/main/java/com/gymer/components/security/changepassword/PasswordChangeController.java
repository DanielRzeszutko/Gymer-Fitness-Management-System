package com.gymer.components.security.changepassword;

import com.gymer.resources.credential.CredentialService;
import com.gymer.resources.credential.entity.Credential;
import com.gymer.resources.user.UserService;
import com.gymer.resources.user.entity.User;
import com.gymer.components.security.changepassword.entity.PasswordDetails;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@AllArgsConstructor
public class PasswordChangeController {

    private final PasswordEncoder passwordEncoder;
    private final UserService userService;
    private final CredentialService credentialService;

    @PutMapping("/api/users/{userId}/password")
    @PreAuthorize("hasRole('ADMIN') or (hasRole('USER') and @accountOwnerValidator.isOwnerLoggedIn(#userId))")
    public void changeUsersPassword(@RequestBody PasswordDetails passwordDetails, @PathVariable Long userId) {
        User user = userService.getElementById(userId);
        Credential credential = credentialService.getElementById(user.getCredential().getId());

        if (!passwordEncoder.matches(passwordDetails.getOldPassword(), credential.getPassword())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT);
        }

        if (passwordDetails.getNewPassword() == null || passwordDetails.getNewPassword().length() < 3) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }

        String newPassword = passwordEncoder.encode(passwordDetails.getNewPassword());
        credential.setPassword(newPassword);
        credentialService.updateElement(credential);
    }

}
