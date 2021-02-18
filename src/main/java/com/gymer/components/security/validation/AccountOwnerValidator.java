package com.gymer.components.security.validation;

import com.gymer.api.credential.entity.Credential;
import com.gymer.api.credential.entity.Role;
import com.gymer.api.partner.PartnerService;
import com.gymer.api.partner.entity.Partner;
import com.gymer.api.partner.entity.PartnerDTO;
import com.gymer.api.slot.SlotService;
import com.gymer.api.slot.entity.Slot;
import com.gymer.api.user.entity.UserDTO;
import com.gymer.components.security.common.entity.AccountDetails;
import com.gymer.components.session.SessionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class AccountOwnerValidator {

    private final SessionService service;
    private final PartnerService partnerService;
    private final SlotService slotService;

    @Autowired
    public AccountOwnerValidator(SessionService service, PartnerService partnerService, SlotService slotService) {
        this.service = service;
        this.partnerService = partnerService;
        this.slotService = slotService;
    }

    public boolean isGuest() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return service.isPrincipalNonExist(authentication) || service.isLoggedAsRole(authentication, Role.ADMIN);
    }

    public boolean isOwnerLoggedIn(Long accountId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (service.isPrincipalNonExist(authentication)) return false;
        if (service.isLoggedAsRole(authentication, Role.ADMIN)) return true;

        Credential credential = ((AccountDetails) authentication.getPrincipal()).getCredential();

        if (service.isLoggedAsRole(authentication, Role.PARTNER)) {
            PartnerDTO partner = service.getActivePartnerAccountFromCredentials(credential);
            return partner.getId().equals(accountId);
        }

        if (service.isLoggedAsRole(authentication, Role.USER)) {
            UserDTO user = service.getActiveUserAccountFromCredentials(credential);
            return user.getId().equals(accountId);
        }

        return false;
    }

    public boolean isOwnerManipulatingSlot(Long slotId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (service.isPrincipalNonExist(authentication)) return false;
        if (service.isLoggedAsRole(authentication, Role.ADMIN)) return true;
        if (!service.isLoggedAsRole(authentication, Role.PARTNER)) return false;

        Credential credential = ((AccountDetails) authentication.getPrincipal()).getCredential();
        PartnerDTO partnerDTO = service.getActivePartnerAccountFromCredentials(credential);
        Slot slot = slotService.getElementById(slotId);
        Partner partner = partnerService.findPartnerContainingSlot(slot);
        return partner.getId().equals(partnerDTO.getId());
    }

}
