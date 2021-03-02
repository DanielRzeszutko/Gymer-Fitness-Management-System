package com.gymer.security.oauth2;

import com.gymer.common.crudresources.credential.CredentialService;
import com.gymer.common.crudresources.user.UserService;
import com.gymer.security.common.handler.LoginSuccessHandler;
import lombok.AllArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
@AllArgsConstructor
public class OAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final LoginSuccessHandler successHandler;
    private final CredentialService credentialService;
    private final UserService userService;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {
        // sprawdz czy uzytkownik z podanym providerem istnieje kontynuuj
        // sprawdz czy uzytkownik z takim mailem istnieje z rolą USER dopisz provider id
        // załóż nowe konto ze statusem active
        // ustaw authentication jako email i role z credentials !IMPORTANT!

        successHandler.onAuthenticationSuccess(request, response, authentication);
    }

}
