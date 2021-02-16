package com.gymer.components.register;

import com.gymer.api.credential.entity.Credential;
import com.gymer.api.credential.entity.Role;
import com.gymer.api.partner.PartnerService;
import com.gymer.api.user.UserService;
import com.gymer.api.user.entity.User;
import com.gymer.components.common.entity.JsonResponse;
import com.gymer.components.register.entity.UserRegisterDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.sql.Timestamp;

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
	public JsonResponse registerUser(@RequestBody UserRegisterDetails userDetails) {
		if (!userDetails.getPassword().equals(userDetails.getConfirmPassword())) {
			return new JsonResponse("Passwords do not match.", true);
		}
		else if (userService.isUserExistsByEmail(userDetails.getEmail())) {
			return new JsonResponse("Account with this email already exists.", true);
		}
		String codedPassword = passwordEncoder.encode(userDetails.getPassword());
		Timestamp timestamp = new Timestamp(new java.util.Date().getTime());
		Credential credential = new Credential(userDetails.getEmail(),
				codedPassword, "", Role.USER, false, timestamp);
		User user = new User("", "", credential);
		userService.updateElement(user);
			return new JsonResponse("Registered successfully.", false);

	}

	@PostMapping("/partner")
	public void registerPartner(@RequestBody UserRegisterDetails registerCredentials) {


	}
}
