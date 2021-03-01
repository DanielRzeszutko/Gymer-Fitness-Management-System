package com.gymer.components.security.session;

import com.gymer.resources.credential.CredentialService;
import com.gymer.resources.credential.entity.Credential;
import com.gymer.resources.credential.entity.Role;
import com.gymer.resources.partner.PartnerService;
import com.gymer.resources.partner.entity.Partner;
import com.gymer.resources.partner.entity.PartnerDTO;
import com.gymer.resources.user.UserService;
import com.gymer.resources.user.entity.User;
import com.gymer.resources.user.entity.UserDTO;
import com.gymer.components.security.session.entity.ActiveAccount;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public final class SessionService {

    private final PartnerService partnerService;
    private final UserService userService;
    private final CredentialService credentialService;

    @Autowired
    public SessionService(PartnerService partnerService, UserService userService, CredentialService credentialService) {
        this.partnerService = partnerService;
        this.userService = userService;
        this.credentialService = credentialService;
    }

    public ActiveAccount getActiveAccountIdFromDetails(Authentication authentication) {
        try {
            Credential credential = credentialService.getCredentialByEmail((String) authentication.getPrincipal());
            Long activeAccountId = credential.getRole().equals(Role.PARTNER)
                    ? partnerService.getByCredentials(credential).getId()
                    : userService.getByCredentials(credential).getId();
            return new ActiveAccount(credential, activeAccountId);
        } catch (ResponseStatusException e) {
            return null;
        }
    }

    public boolean isLoggedAsRole(Authentication authentication, Role role) {
        if (isPrincipalNonExist(authentication)) return false;
        try {
            Credential credential = credentialService.getCredentialByEmail((String) authentication.getPrincipal());
            return credential.getRole().equals(role);
        } catch (ResponseStatusException e) {
            return false;
        }
    }

    public PartnerDTO getActivePartnerAccountFromCredentials(Authentication authentication) {
        try {
            Credential credential = credentialService.getCredentialByEmail((String) authentication.getPrincipal());
            Partner partner = partnerService.getByCredentials(credential);
            return new PartnerDTO(partner);
        } catch (ResponseStatusException e) {
            return null;
        }
    }

    public UserDTO getActiveUserAccountFromCredentials(Authentication authentication) {
        try {
            Credential credential = credentialService.getCredentialByEmail((String) authentication.getPrincipal());
            User user = userService.getByCredentials(credential);
            return new UserDTO(user);
        } catch (ResponseStatusException e) {
            return null;
        }
    }

    public boolean isPrincipalNonExist(Authentication authentication) {
        return authentication == null || authentication.getPrincipal() == null;
    }

}
