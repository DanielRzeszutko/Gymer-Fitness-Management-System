package com.gymer.components.login;

import com.gymer.components.common.entity.JsonResponse;
import com.gymer.components.login.entity.LoginDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class LoginController {

	@GetMapping("/login")
	public String getLoginForm() {
		return "login form";
	}

	@PostMapping("/login")
	public JsonResponse login(@RequestBody LoginDetails loginDetails) {
		return new JsonResponse("Login successfully", false);
	}

}
