package com.gymer.accountsession;

import com.gymer.common.resources.credential.entity.Role;
import com.gymer.common.resources.partner.entity.PartnerDTO;
import com.gymer.common.resources.user.entity.UserDTO;
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

    private final SessionService service;

    @GetMapping("/api/me")
    @PreAuthorize("hasRole('USER') or hasRole('PARTNER')")
    public ActiveAccount getActiveAccount(Authentication authentication) {
        if (service.isPrincipalNonExist(authentication))
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User or partner not logged in.");
        return service.getActiveAccountIdFromDetails(authentication);
    }

    @GetMapping("/api/me/partner")
    @PreAuthorize("hasRole('PARTNER')")
    public PartnerDTO getActivePartner(Authentication authentication) {
        if (!service.isLoggedAsRole(authentication, Role.PARTNER))
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Partner not logged in.");
        return service.getActivePartnerAccountFromCredentials(authentication);
    }

    @GetMapping("/api/me/user")
    @PreAuthorize("hasRole('USER')")
    public UserDTO getActiveUser(Authentication authentication) {
        if (!service.isLoggedAsRole(authentication, Role.USER))
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not logged in.");
        return service.getActiveUserAccountFromCredentials(authentication);
    }

}
