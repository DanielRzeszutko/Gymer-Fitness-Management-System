package com.gymer.security.session;

import com.gymer.api.credential.entity.Credential;
import com.gymer.api.credential.entity.Role;
import com.gymer.api.partner.PartnerService;
import com.gymer.api.partner.entity.Partner;
import com.gymer.api.partner.entity.PartnerDTO;
import com.gymer.api.user.UserService;
import com.gymer.api.user.entity.User;
import com.gymer.api.user.entity.UserDTO;
import com.gymer.security.common.entity.AccountDetails;
import com.gymer.security.session.entity.ActiveAccount;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
public final class SessionService {

    private final PartnerService partnerService;
    private final UserService userService;

    @Autowired
    public SessionService(PartnerService partnerService, UserService userService) {
        this.partnerService = partnerService;
        this.userService = userService;
    }

    public ActiveAccount getActiveAccountIdFromDetails(AccountDetails details) {
        Credential credential = details.getCredential();
        Long activeAccountId = credential.getRole().equals(Role.PARTNER)
                ? partnerService.getByCredentials(credential).getId()
                : userService.getByCredentials(credential).getId();
        return new ActiveAccount(activeAccountId, credential);
    }

    public boolean isAccountNotLoggedOrEqualRole(Authentication authentication, Role role) {
        return !isPrincipalExist(authentication) || !((AccountDetails) authentication.getPrincipal()).getCredential().getRole().equals(role);
    }

    public PartnerDTO getActivePartnerAccountFromCredentials(Credential credential) {
        Partner partner = partnerService.getByCredentials(credential);
        return new PartnerDTO(partner);
    }

    public UserDTO getActiveUserAccountFromCredentials(Credential credential) {
        User user = userService.getByCredentials(credential);
        return new UserDTO(user);
    }

    private boolean isPrincipalExist(Authentication authentication) {
        return authentication == null || authentication.getPrincipal() == null;
    }

}
