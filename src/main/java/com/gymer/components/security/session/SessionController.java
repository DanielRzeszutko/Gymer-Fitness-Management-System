package com.gymer.components.security.session;

import com.gymer.api.credential.entity.Role;
import com.gymer.api.partner.entity.PartnerDTO;
import com.gymer.api.user.entity.UserDTO;
import com.gymer.components.security.common.entity.AccountDetails;
import com.gymer.components.security.session.entity.ActiveAccount;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
public class SessionController {

    private final SessionService service;

    @Autowired
    public SessionController(SessionService sessionService) {
        this.service = sessionService;
    }

    @GetMapping("/api/me")
    @PreAuthorize("hasRole('USER') or hasRole('PARTNER')")
    public ActiveAccount getActiveAccount(Authentication authentication) {
        if (service.isPrincipalNonExist(authentication)) throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        return service.getActiveAccountIdFromDetails(authentication);
    }

    @GetMapping("/api/me/partner")
    @PreAuthorize("hasRole('PARTNER')")
    public PartnerDTO getActivePartner(Authentication authentication) {
        if (!service.isLoggedAsRole(authentication, Role.PARTNER))
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        return service.getActivePartnerAccountFromCredentials(authentication);
    }

    @GetMapping("/api/me/user")
    @PreAuthorize("hasRole('USER')")
    public UserDTO getActiveUser(Authentication authentication) {
        if (!service.isLoggedAsRole(authentication, Role.USER))
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        return service.getActiveUserAccountFromCredentials(authentication);
    }

}
