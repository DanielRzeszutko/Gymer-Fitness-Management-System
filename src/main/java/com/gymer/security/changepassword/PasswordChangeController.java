package com.gymer.security.changepassword;

import com.gymer.common.crudresources.credential.entity.Credential;
import com.gymer.common.crudresources.user.UserService;
import com.gymer.common.crudresources.user.entity.User;
import com.gymer.common.entity.JsonResponse;
import com.gymer.security.changepassword.entity.PasswordDetails;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.Objects;

@RestController
@AllArgsConstructor
class PasswordChangeController {

    private final PasswordChangeService passwordChangeService;
    private final UserService userService;

    @PutMapping("/api/users/{userId}/password")
    @PreAuthorize("hasRole('ADMIN') or (hasRole('USER') and @accountOwnerValidator.isOwnerLoggedIn(#userId))")
    public void changeUsersPassword(@RequestBody PasswordDetails passwordDetails, @PathVariable Long userId) {
        Credential credential = passwordChangeService.getUsersCredential(userId);
        User user = userService.getByCredentials(credential);

        if (user.getProviderId() != null && user.getProviderId().length() > 0) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You are logged by Google account, you can't change your password.");
        }

        if (passwordChangeService.isPasswordNotEqual(passwordDetails, credential)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Passwords are not equal.");
        }

        if (passwordDetails.getNewPassword() == null || passwordDetails.getNewPassword().length() < 3) {
            throw new ResponseStatusException(HttpStatus.NOT_ACCEPTABLE, "Invalid new password");
        }

        passwordChangeService.changePassword(passwordDetails, credential);
        throw new ResponseStatusException(HttpStatus.OK, "Successfully changed password");
    }

    @PutMapping("/api/partners/{partnerId}/password")
    @PreAuthorize("hasRole('ADMIN') or (hasRole('PARTNER') and @accountOwnerValidator.isOwnerLoggedIn(#partnerId))")
    public JsonResponse changePartnersPassword(@RequestBody PasswordDetails passwordDetails, @PathVariable Long partnerId) {
        throw new ResponseStatusException(HttpStatus.NOT_IMPLEMENTED, "Not implemented yet!");
    }

}
