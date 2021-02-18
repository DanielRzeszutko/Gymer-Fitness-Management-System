package com.gymer.security.session;

import com.gymer.api.credential.CredentialService;
import com.gymer.api.credential.entity.Credential;
import com.gymer.api.credential.entity.Role;
import com.gymer.api.partner.PartnerService;
import com.gymer.api.partner.entity.Partner;
import com.gymer.api.user.UserService;
import com.gymer.api.user.entity.User;
import com.gymer.security.common.entity.AccountDetails;
import com.gymer.security.session.entity.ActiveAccount;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SessionService {

    private final PartnerService partnerService;
    private final UserService userService;
    private final CredentialService credentialService;

    @Autowired
    public SessionService(PartnerService partnerService, UserService userService, CredentialService credentialService) {
        this.partnerService = partnerService;
        this.userService = userService;
        this.credentialService = credentialService;
    }

    public ActiveAccount getActiveAccountFromDetails(AccountDetails details) {
        Credential credential = details.getCredential();
        if (credential.getRole().equals(Role.PARTNER)) {
            Partner partner = partnerService.getByCredentials(credential);
            return new ActiveAccount(partner.getId(), credential.getRole(), credential);
        }
        User user = userService.getByCredentials(credential);
        return new ActiveAccount(user.getId(), credential.getRole(), credential);
    }

}
