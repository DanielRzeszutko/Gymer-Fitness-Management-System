package com.gymer.security.validation;

import com.gymer.crudresources.credential.entity.Role;
import com.gymer.crudresources.partner.PartnerService;
import com.gymer.crudresources.partner.entity.Partner;
import com.gymer.crudresources.partner.entity.PartnerDTO;
import com.gymer.crudresources.slot.SlotService;
import com.gymer.crudresources.slot.entity.Slot;
import com.gymer.crudresources.user.entity.UserDTO;
import com.gymer.security.session.SessionService;
import lombok.AllArgsConstructor;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
@AllArgsConstructor
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class AccountOwnerValidator {

    private final SessionService service;
    private final PartnerService partnerService;
    private final SlotService slotService;

    public boolean isGuest() {
        Authentication authentication = getActiveAuthentication();
        return isNoOneLoggedInOrIsAdmin(authentication);
    }

    public boolean isOwnerLoggedIn(Long accountId) {
        Authentication authentication = getActiveAuthentication();

        if (service.isPrincipalNonExist(authentication)) return false;
        if (service.isLoggedAsRole(authentication, Role.ADMIN)) return true;

        if (service.isLoggedAsRole(authentication, Role.PARTNER)) {
            PartnerDTO partner = service.getActivePartnerAccountFromCredentials(authentication);
            return Objects.requireNonNull(partner).getId().equals(accountId);
        }

        if (service.isLoggedAsRole(authentication, Role.USER)) {
            UserDTO user = service.getActiveUserAccountFromCredentials(authentication);
            return Objects.requireNonNull(user).getId().equals(accountId);
        }

        return false;
    }

    public boolean isOwnerManipulatingSlot(Long slotId) {
        Authentication authentication = getActiveAuthentication();

        if (service.isPrincipalNonExist(authentication)) return false;
        if (service.isLoggedAsRole(authentication, Role.ADMIN)) return true;
        if (!service.isLoggedAsRole(authentication, Role.PARTNER)) return false;

        PartnerDTO partnerDTO = service.getActivePartnerAccountFromCredentials(authentication);
        Slot slot = slotService.getElementById(slotId);
        Partner partner = partnerService.findPartnerContainingSlot(slot);

        return Objects.requireNonNull(partnerDTO).getId().equals(partner.getId());
    }

    private Authentication getActiveAuthentication() {
        return SecurityContextHolder.getContext().getAuthentication();
    }

    private boolean isNoOneLoggedInOrIsAdmin(Authentication authentication) {
        return service.isPrincipalNonExist(authentication) || service.isLoggedAsRole(authentication, Role.ADMIN);
    }

}
