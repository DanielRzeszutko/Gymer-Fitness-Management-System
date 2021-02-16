package com.gymer.components.register;

import com.gymer.components.common.entity.JsonResponse;
import com.gymer.components.register.entity.RegistrationDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/registration")
public class RegistrationController {

	private final RegistrationService registrationService;


	@Autowired
	public RegistrationController( RegistrationService registrationService) {
		this.registrationService = registrationService;
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
