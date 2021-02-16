package com.gymer.components.register;

import com.gymer.api.address.entity.Address;
import com.gymer.api.credential.entity.Credential;
import com.gymer.api.credential.entity.Role;
import com.gymer.api.partner.PartnerService;
import com.gymer.api.partner.entity.Partner;
import com.gymer.api.user.UserService;
import com.gymer.api.user.entity.User;
import com.gymer.components.common.entity.JsonResponse;
import com.gymer.components.register.entity.RegisterDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.sql.Timestamp;
import java.util.Collections;

@RestController
@RequestMapping("/registration")
public class RegistrationController {

	private final PasswordEncoder passwordEncoder;
	private final UserService userService;
	private final PartnerService partnerService;

	@Autowired
	public RegistrationController(PasswordEncoder passwordEncoder, UserService userService, PartnerService partnerService) {
		this.passwordEncoder = passwordEncoder;
		this.userService = userService;
		this.partnerService = partnerService;
	}

	@PostMapping("/user")
	@ResponseStatus(HttpStatus.OK)
	public JsonResponse registerUser(@RequestBody RegisterDetails registerDetails) {
		JsonResponse response = getJsonResponse(registerDetails);
		if (response.isError()) {
			return response;
		}

		Credential credential = getCredentialBy(registerDetails, Role.USER);
		User user = new User("", "", credential);
		userService.updateElement(user);
		return response;
	}

	@PostMapping("/partner")
	public JsonResponse registerPartner(@RequestBody RegisterDetails registerDetails) {
		JsonResponse response = getJsonResponse(registerDetails);
		if (response.isError()) {
			return response;
		}

		Credential credential = getCredentialBy(registerDetails, Role.PARTNER);
		Address address = new Address("","","","");
		Partner partner = new Partner("","","","","", credential, address,
				Collections.emptyList(), Collections.emptyList(), Collections.emptyList());
		partnerService.updateElement(partner);
		return response;
	}

	private JsonResponse getJsonResponse(RegisterDetails userDetails) {
		if (!userDetails.getPassword().equals(userDetails.getConfirmPassword())) {
			return new JsonResponse("Passwords do not match.", true);
		}
		else if (userService.isUserExistsByEmail(userDetails.getEmail())) {
			return new JsonResponse("Account with this email already exists.", true);
		}
		return new JsonResponse("Registered successfully.", false);
	}

	private Credential getCredentialBy(RegisterDetails userDetails, Role role) {
		String codedPassword = passwordEncoder.encode(userDetails.getPassword());
		Timestamp timestamp = new Timestamp(new java.util.Date().getTime());
		return new Credential(userDetails.getEmail(),
				codedPassword, "", role, false, timestamp);
	}

}
