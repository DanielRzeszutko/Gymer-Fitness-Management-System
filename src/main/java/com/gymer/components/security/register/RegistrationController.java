package com.gymer.components.security.register;

import com.gymer.resources.credential.entity.Role;
import com.gymer.components.common.entity.JsonResponse;
import com.gymer.components.security.register.entity.RegistrationDetails;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
public class RegistrationController {

    private final RegistrationService registrationService;

    @PostMapping("/api/registration/user")
    public JsonResponse registerUser(@RequestBody RegistrationDetails registrationDetails) {
        return registrationService.registerAccount(registrationDetails, Role.USER);
    }

    @PostMapping("/api/registration/partner")
    public JsonResponse registerPartner(@RequestBody RegistrationDetails registrationDetails) {
        return registrationService.registerAccount(registrationDetails, Role.PARTNER);
    }

}
