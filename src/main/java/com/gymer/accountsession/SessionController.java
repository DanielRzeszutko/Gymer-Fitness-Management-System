package com.gymer.accountsession;

import com.gymer.commoncomponents.accountvalidator.AccountOwnerValidator;
import com.gymer.commoncomponents.languagepack.LanguageComponent;
import com.gymer.commonresources.credential.entity.Role;
import com.gymer.commonresources.partner.entity.PartnerDTO;
import com.gymer.commonresources.user.entity.UserDTO;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@AllArgsConstructor
class SessionController {

    private final AccountOwnerValidator validator;
    private final LanguageComponent language;

    @GetMapping("/api/me")
    @PreAuthorize("hasRole('USER') or hasRole('PARTNER')")
    public Object getActiveAccount(Authentication authentication) {
        if (validator.isPrincipalNonExist(authentication))
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, language.notValidAccount());
        return validator.getActiveAccountIdFromDetails(authentication);
    }

    @GetMapping("/api/me/partner")
    @PreAuthorize("hasRole('PARTNER')")
    public PartnerDTO getActivePartner(Authentication authentication) {
        if (!validator.isLoggedAsRole(authentication, Role.PARTNER))
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, language.notValidAccount());
        return validator.getActivePartnerAccountFromCredentials(authentication);
    }

    @GetMapping("/api/me/user")
    @PreAuthorize("hasRole('USER')")
    public UserDTO getActiveUser(Authentication authentication) {
        if (!validator.isLoggedAsRole(authentication, Role.USER))
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, language.notValidAccount());
        return validator.getActiveUserAccountFromCredentials(authentication);
    }

}
