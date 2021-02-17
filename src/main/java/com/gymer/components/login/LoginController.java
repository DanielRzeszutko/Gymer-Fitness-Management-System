package com.gymer.components.login;

import com.gymer.components.common.entity.JsonResponse;
import com.gymer.components.login.entity.AccountDetails;
import com.gymer.components.login.entity.LoginDetails;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class LoginController {

	private LoginService loginService;

	public LoginController(LoginService loginService) {
		this.loginService = loginService;
	}

	@GetMapping("/login")
	public String getLoginForm() {
		return "login form";
	}

	@PostMapping("/login")
	public JsonResponse login(@RequestBody LoginDetails loginDetails) {
		System.out.println("LOGIN");
		UserDetails accountDetails = loginService.loadUserByUsername(loginDetails.getEmail());
		System.out.println(accountDetails.getPassword());

		return new JsonResponse("Login successfully", false);
	}

}
