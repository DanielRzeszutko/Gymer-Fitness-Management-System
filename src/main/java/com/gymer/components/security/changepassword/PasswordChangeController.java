package com.gymer.components.security.changepassword;

import com.gymer.api.credential.CredentialService;
import com.gymer.api.credential.entity.Credential;
import com.gymer.api.user.UserService;
import com.gymer.api.user.entity.User;
import com.gymer.components.security.changepassword.entity.PasswordDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping
public class PasswordChangeController {

    private final PasswordEncoder passwordEncoder;
    private final UserService userService;

    @Autowired
    public PasswordChangeController(PasswordEncoder passwordEncoder, UserService userService) {
        this.passwordEncoder = passwordEncoder;
        this.userService = userService;
    }

    @PutMapping("/api/users/{userId}/password")
    @PreAuthorize("hasRole('ADMIN') or (hasRole('USER') and @accountOwnerValidator.isOwnerLoggedIn(#userId))")
    public void changeUsersPassword(@RequestBody PasswordDetails passwordDetails, @PathVariable Long userId) {
        User user = userService.getElementById(userId);
        Credential credentials = user.getCredential();

        if (!passwordEncoder.matches(passwordDetails.getOldPassword(), credentials.getPassword())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT);
        }
        credentials.setPassword(passwordEncoder.encode(passwordDetails.getNewPassword()));
        userService.updateElement(user);
    }

}
