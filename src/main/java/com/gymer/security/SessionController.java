package com.gymer.security;

import com.gymer.api.credential.entity.Credential;
import com.gymer.api.partner.PartnerService;
import com.gymer.api.partner.entity.Partner;
import com.gymer.api.user.UserService;
import com.gymer.api.user.entity.User;
import com.gymer.security.entity.LoginCredentials;
import com.gymer.security.entity.RegisterCredentials;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
public class SessionController {

    private final UserService userService;
    private final PartnerService partnerService;

    @Autowired
    public SessionController(UserService userService, PartnerService partnerService) {
        this.userService = userService;
        this.partnerService = partnerService;
    }

    @PostMapping("/api/login")
    public void login(@RequestBody LoginCredentials loginCredentials) {

    }
//
//    @PostMapping("/api/register")
//    public void register(@RequestBody RegisterCredentials registerCredentials) {
//
//    }

    @GetMapping("/api/logout")
    public void logout() {

    }

    @GetMapping("/api/me")
    public Object getActiveUser(@AuthenticationPrincipal Authentication authentication) {
        if (authentication == null || authentication.getPrincipal() == null) return null;
        Credential credential = (Credential) authentication.getPrincipal();
        User user = userService.getByCredentials(credential).orElse(null);
        if (user != null) return user;
        return partnerService.getByCredentials(credential);
    }

}
