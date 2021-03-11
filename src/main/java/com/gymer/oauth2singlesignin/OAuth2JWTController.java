package com.gymer.oauth2singlesignin;

import com.gymer.commoncomponents.jwtcreator.JWTCreatorComponent;
import com.gymer.commoncomponents.languagepack.LanguageComponent;
import com.gymer.commonresources.credential.CredentialService;
import com.gymer.commonresources.credential.entity.Credential;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collections;

@RestController
@RequiredArgsConstructor
class OAuth2JWTController {

    private final CredentialService credentialService;
    private final JWTCreatorComponent jwtCreatorComponent;
    private final LanguageComponent language;
    private final RedirectStrategy redirectStrategy = new DefaultRedirectStrategy();

    @GetMapping("/api/google")
    public void obtainJWTIfLoggedByGoogle(HttpServletResponse response, Authentication authentication) {
        if (authentication == null || authentication.getPrincipal() == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, language.userNotLoggedViaSingleSignIn());
        }

        String userEmail = (String) authentication.getPrincipal();
        Credential credential = credentialService.getCredentialByEmail(userEmail);
        GrantedAuthority authority = new SimpleGrantedAuthority("ROLE_" + credential.getRole());
        authentication = new UsernamePasswordAuthenticationToken(userEmail, null, Collections.singletonList(authority));
        response.setHeader("Authorization", jwtCreatorComponent.createToken(authentication));
        response.setStatus(HttpStatus.OK.value());
        response.setContentType("application/json");
    }

    @GetMapping("/api/google-auth")
    public void authorizeViaGoogle(HttpServletRequest request, HttpServletResponse response) throws IOException {
        redirectStrategy.sendRedirect(request, response, "../oauth2/authorization/google");
    }

}
