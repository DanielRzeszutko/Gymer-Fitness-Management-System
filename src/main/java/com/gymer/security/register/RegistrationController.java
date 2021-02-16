package com.gymer.security.register;

import com.gymer.api.credential.entity.Credential;
import com.gymer.api.credential.entity.Role;
import com.gymer.api.partner.PartnerService;
import com.gymer.api.user.UserService;
import com.gymer.api.user.entity.User;
import com.gymer.security.entity.RegisterCredentials;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

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
	public void registerUser(@RequestBody RegisterCredentials registerCredentials) {
		if (!registerCredentials.getPassword().equals(registerCredentials.getConfirmPassword())) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
		}

		Credential credential = new Credential(registerCredentials.getEmail(),
				passwordEncoder.encode(registerCredentials.getPassword()),
				"", Role.USER, false);
		User user = new User("", "", credential);
		userService.updateElement(user);

	}

	@PostMapping("/partner")
	public void registerPartner(@RequestBody RegisterCredentials registerCredentials) {


	}
}
