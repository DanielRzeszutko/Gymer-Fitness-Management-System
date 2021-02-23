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
    public ActiveAccount getActiveAccount(Authentication authentication) {
        if (service.isPrincipalNonExist(authentication)) return null;
        return service.getActiveAccountIdFromDetails(authentication);
    }

    @GetMapping("/api/me/partner")
    public PartnerDTO getActivePartner(Authentication authentication) {
        if (!service.isLoggedAsRole(authentication, Role.PARTNER)) return null;
        return service.getActivePartnerAccountFromCredentials(authentication);
    }

    @GetMapping("/api/me/user")
    public UserDTO getActiveUser(Authentication authentication) {
        if (!service.isLoggedAsRole(authentication, Role.USER)) return null;
        return service.getActiveUserAccountFromCredentials(authentication);
    }

}
