package com.gymer.security.register;

import com.gymer.components.common.entity.JsonResponse;
import com.gymer.security.register.entity.RegistrationDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/registration")
public class RegistrationController {

    private final RegistrationService registrationService;


    @Autowired
    public RegistrationController(RegistrationService registrationService) {
        this.registrationService = registrationService;
    }

    @GetMapping
    public String getRegisterForm() {
        return "register form";
    }

    @PostMapping("/user")
    public JsonResponse registerUser(@RequestBody RegistrationDetails registrationDetails) {
        return registrationService.registerUser(registrationDetails);
    }

    @PostMapping("/partner")
    public JsonResponse registerPartner(@RequestBody RegistrationDetails registrationDetails) {
        return registrationService.registerPartner(registrationDetails);
    }


}
