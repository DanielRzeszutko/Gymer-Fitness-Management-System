package com.gymer.oauth2singlesignin;

import com.gymer.commoncomponents.languagepack.LanguageComponent;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
@RequiredArgsConstructor
class OAuth2FailureHandler extends SimpleUrlAuthenticationFailureHandler {

    private final LanguageComponent language;

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) {
        throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, language.cannotAuthorizeSingleSignIn());
    }

}
