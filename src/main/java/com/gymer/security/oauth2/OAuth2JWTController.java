package com.gymer.security.oauth2;

import com.gymer.common.crudresources.credential.CredentialService;
import com.gymer.common.crudresources.credential.entity.Credential;
import com.gymer.common.entity.JsonResponse;
import com.gymer.security.common.entity.AccountDetails;
import com.gymer.security.login.LoginSuccessHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@RestController
@RequiredArgsConstructor
public class OAuth2JWTController {

    private final CredentialService credentialService;
    private final LoginSuccessHandler loginSuccessHandler;
    private final RedirectStrategy redirectStrategy = new DefaultRedirectStrategy();

    @GetMapping("/api/google")
    public void obtainJWTIfLoggedByGoogle(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        try {
            String userEmail = (String) authentication.getPrincipal();
            Credential credential = credentialService.getCredentialByEmail(userEmail);
            AccountDetails accountDetails = new AccountDetails(credential);
            UsernamePasswordAuthenticationToken authRequest = new UsernamePasswordAuthenticationToken(accountDetails, credential.getPassword());
            loginSuccessHandler.onAuthenticationSuccess(request, response, authRequest);
        } catch (IOException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not logged by Google account.");
        }
    }

    @GetMapping("/api/google-auth")
    public void redirectToGoogleLoginPage(HttpServletRequest request, HttpServletResponse response) throws IOException {
        redirectStrategy.sendRedirect(request, response, "../oauth2/authorization/google");
    }

}
