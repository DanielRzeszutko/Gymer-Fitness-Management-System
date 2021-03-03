package com.gymer.commoncomponents.accountvalidator;

import com.gymer.commonresources.credential.CredentialService;
import com.gymer.commonresources.credential.entity.Credential;
import com.gymer.commonresources.credential.entity.Role;
import com.gymer.commonresources.partner.PartnerService;
import com.gymer.commonresources.partner.entity.Partner;
import com.gymer.commonresources.partner.entity.PartnerDTO;
import com.gymer.commonresources.slot.SlotService;
import com.gymer.commonresources.slot.entity.Slot;
import com.gymer.commonresources.user.UserService;
import com.gymer.commonresources.user.entity.User;
import com.gymer.commonresources.user.entity.UserDTO;
import lombok.AllArgsConstructor;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import java.util.Objects;

@Component
@AllArgsConstructor
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class AccountOwnerValidator {

    private final PartnerService partnerService;
    private final SlotService slotService;
    private final UserService userService;
    private final CredentialService credentialService;

    public boolean isGuest() {
        Authentication authentication = getActiveAuthentication();
        return isNoOneLoggedInOrIsAdmin(authentication);
    }

    public boolean isOwnerLoggedIn(Long accountId) {
        Authentication authentication = getActiveAuthentication();

        if (isPrincipalNonExist(authentication)) return false;
        if (isLoggedAsRole(authentication, Role.ADMIN)) return true;

        if (isLoggedAsRole(authentication, Role.PARTNER)) {
            PartnerDTO partner = getActivePartnerAccountFromCredentials(authentication);
            return Objects.requireNonNull(partner).getId().equals(accountId);
        }

        if (isLoggedAsRole(authentication, Role.USER)) {
            UserDTO user = getActiveUserAccountFromCredentials(authentication);
            return Objects.requireNonNull(user).getId().equals(accountId);
        }

        return false;
    }

    public boolean isOwnerManipulatingSlot(Long slotId) {
        Authentication authentication = getActiveAuthentication();

        if (isPrincipalNonExist(authentication)) return false;
        if (isLoggedAsRole(authentication, Role.ADMIN)) return true;
        if (!isLoggedAsRole(authentication, Role.PARTNER)) return false;

        PartnerDTO partnerDTO = getActivePartnerAccountFromCredentials(authentication);
        Slot slot = slotService.getElementById(slotId);
        Partner partner = partnerService.findPartnerContainingSlot(slot);

        return Objects.requireNonNull(partnerDTO).getId().equals(partner.getId());
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

    private Authentication getActiveAuthentication() {
        return SecurityContextHolder.getContext().getAuthentication();
    }

    private boolean isNoOneLoggedInOrIsAdmin(Authentication authentication) {
        return isPrincipalNonExist(authentication) || isLoggedAsRole(authentication, Role.ADMIN);
    }

}
