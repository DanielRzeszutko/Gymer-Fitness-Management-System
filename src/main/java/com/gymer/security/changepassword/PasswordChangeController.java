package com.gymer.security.changepassword;

import com.gymer.common.entity.JsonResponse;
import com.gymer.resources.credential.entity.Credential;
import com.gymer.security.changepassword.entity.PasswordDetails;
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

    @PutMapping("/api/users/{userId}/password")
    @PreAuthorize("hasRole('ADMIN') or (hasRole('USER') and @accountOwnerValidator.isOwnerLoggedIn(#userId))")
    public JsonResponse changeUsersPassword(@RequestBody PasswordDetails passwordDetails, @PathVariable Long userId) {
        Credential credential = passwordChangeService.getUsersCredential(userId);

        if (passwordChangeService.isPasswordNotEqual(passwordDetails, credential)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Passwords are not equal");
        }

        if (passwordDetails.getNewPassword() == null || passwordDetails.getNewPassword().length() < 3) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid new password");
        }

        passwordChangeService.changePassword(passwordDetails, credential);
        return JsonResponse.validMessage("Successfully changed password");
    }

}
