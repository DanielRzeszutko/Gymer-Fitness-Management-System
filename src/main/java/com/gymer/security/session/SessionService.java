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

    public PartnerDTO getActivePartnerAccountFromCredentials(Credential credential) {
        Partner partner = partnerService.getByCredentials(credential);
        return new PartnerDTO(partner);
    }

    public UserDTO getActiveUserAccountFromCredentials(Credential credential) {
        User user = userService.getByCredentials(credential);
        return new UserDTO(user);
    }



}
