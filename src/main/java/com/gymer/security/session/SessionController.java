package com.gymer.security.session;

import com.gymer.api.credential.entity.Role;
import com.gymer.api.partner.entity.PartnerDTO;
import com.gymer.api.user.entity.UserDTO;
import com.gymer.security.common.entity.AccountDetails;
import com.gymer.security.session.entity.ActiveAccount;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
public class SessionController {

    private final SessionService sessionService;

    @Autowired
    public SessionController(SessionService sessionService) {
        this.sessionService = sessionService;
    }

    @GetMapping("/me")
    @PreAuthorize("hasRole('PARTNER') or hasRole('USER')")
    public ActiveAccount getActiveAccount(Authentication authentication) {
        if (isPrincipalExist(authentication)) return null;

        AccountDetails details = (AccountDetails) authentication.getPrincipal();
        if (partnerIsNotLogged(details) && userIsNotLogged(details)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }

        Long activeAccountId = sessionService.getActiveAccountIdFromDetails(details);
        return new ActiveAccount(activeAccountId, details.getCredential());
    }

    @GetMapping("/me/partner")
    @PreAuthorize("hasRole('PARTNER')")
    public PartnerDTO getActivePartner(Authentication authentication) {
        if (isPrincipalExist(authentication)) return null;

        AccountDetails details = (AccountDetails) authentication.getPrincipal();
        if (partnerIsNotLogged(details)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }

        return sessionService.getActivePartnerAccountFromCredentials(details.getCredential());
    }

    @GetMapping("/me/user")
    @PreAuthorize("hasRole('USER')")
    public UserDTO getActiveUser(Authentication authentication) {
        if (isPrincipalExist(authentication)) return null;

        AccountDetails details = (AccountDetails) authentication.getPrincipal();
        if (userIsNotLogged(details)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }

        return sessionService.getActiveUserAccountFromCredentials(details.getCredential());
    }

    private boolean isPrincipalExist(Authentication authentication) {
        return authentication == null || authentication.getPrincipal() == null;
    }

    private boolean partnerIsNotLogged(AccountDetails details) {
        return !details.getCredential().getRole().equals(Role.PARTNER);
    }

    private boolean userIsNotLogged(AccountDetails details) {
        return !details.getCredential().getRole().equals(Role.USER);
    }

}
