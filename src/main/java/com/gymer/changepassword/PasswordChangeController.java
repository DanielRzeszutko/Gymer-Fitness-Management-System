package com.gymer.changepassword;

import com.gymer.commoncomponents.languagepack.LanguageComponent;
import com.gymer.commonresources.credential.entity.Credential;
import com.gymer.commonresources.user.UserService;
import com.gymer.commonresources.user.entity.User;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@AllArgsConstructor
class PasswordChangeController {

    private final PasswordChangeService passwordChangeService;
    private final LanguageComponent language;
    private final UserService userService;

    @PutMapping("/api/users/{userId}/password")
    @PreAuthorize("hasRole('ADMIN') or (hasRole('USER') and @accountOwnerValidator.isOwnerLoggedIn(#userId))")
    public void changeUsersPassword(@RequestBody PasswordDetails passwordDetails, @PathVariable Long userId) {
        Credential credential = passwordChangeService.getUsersCredential(userId);
        User user = userService.getByCredentials(credential);

        if (credential.getPassword() == null || (user.getProviderId() != null && user.getProviderId().length() > 0)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, language.cannotChangePassword());
        }

        if (passwordChangeService.isPasswordNotEqual(passwordDetails, credential)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, language.passwordsDoesntEqual());
        }

        if (passwordDetails.getNewPassword() == null || passwordDetails.getNewPassword().length() < 3) {
            throw new ResponseStatusException(HttpStatus.NOT_ACCEPTABLE, language.invalidNewPassword());
        }

        passwordChangeService.changePassword(passwordDetails, credential);
        throw new ResponseStatusException(HttpStatus.OK, language.passwordChanged());
    }

    @PutMapping("/api/partners/{partnerId}/password")
    @PreAuthorize("hasRole('ADMIN') or (hasRole('PARTNER') and @accountOwnerValidator.isOwnerLoggedIn(#partnerId))")
    public void changePartnersPassword(@RequestBody PasswordDetails passwordDetails, @PathVariable Long partnerId) {
        throw new ResponseStatusException(HttpStatus.NOT_IMPLEMENTED, language.notImplementedYet());
    }

}
