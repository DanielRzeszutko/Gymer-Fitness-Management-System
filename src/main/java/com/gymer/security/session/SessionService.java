package com.gymer.security.session;

import com.gymer.api.credential.entity.Credential;
import com.gymer.api.credential.entity.Role;
import com.gymer.api.partner.PartnerService;
import com.gymer.api.user.UserService;
import com.gymer.security.common.entity.AccountDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SessionService {

    private final PartnerService partnerService;
    private final UserService userService;

    @Autowired
    public SessionService(PartnerService partnerService, UserService userService) {
        this.partnerService = partnerService;
        this.userService = userService;
    }

    public Long getActiveAccountIdFromDetails(AccountDetails details) {
        Credential credential = details.getCredential();
        return credential.getRole().equals(Role.PARTNER)
                ? partnerService.getByCredentials(credential).getId()
                : userService.getByCredentials(credential).getId();
    }

}
