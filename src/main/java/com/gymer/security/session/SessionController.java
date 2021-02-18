package com.gymer.security.session;

import com.gymer.security.common.entity.AccountDetails;
import com.gymer.security.session.entity.ActiveAccount;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SessionController {

    private final SessionService sessionService;

    @Autowired
    public SessionController(SessionService sessionService) {
        this.sessionService = sessionService;
    }

    @GetMapping("/me")
    public ActiveAccount getActiveAccount(Authentication authentication) {
        if (authentication == null || authentication.getPrincipal() == null) return null;
        AccountDetails details = (AccountDetails) authentication.getPrincipal();
        Long activeAccountId = sessionService.getActiveAccountIdFromDetails(details);
        return new ActiveAccount(activeAccountId, details.getCredential());
    }

}
