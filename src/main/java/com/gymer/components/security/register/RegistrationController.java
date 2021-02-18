package com.gymer.components.security.register;

import com.gymer.components.common.entity.JsonResponse;
import com.gymer.components.security.register.entity.RegistrationDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/registration")
public class RegistrationController {

    private final RegistrationService registrationService;

    @Autowired
    public RegistrationController(RegistrationService registrationService) {
        this.registrationService = registrationService;
    }

    @PostMapping("/user")
    public JsonResponse registerUser(@RequestBody RegistrationDetails registrationDetails, HttpServletRequest request) {
        return registrationService.registerUser(registrationDetails, getSiteURL(request));
    }

    @PostMapping("/partner")
    public JsonResponse registerPartner(@RequestBody RegistrationDetails registrationDetails, HttpServletRequest request) {
        return registrationService.registerPartner(registrationDetails, getSiteURL(request));
    }

    private String getSiteURL(HttpServletRequest request) {
        String siteURL = request.getRequestURL().toString();
        return siteURL.replace(request.getServletPath(), "");
    }

}
