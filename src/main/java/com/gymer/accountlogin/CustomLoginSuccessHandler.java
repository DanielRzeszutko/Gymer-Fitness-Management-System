package com.gymer.accountlogin;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gymer.commoncomponents.jwtcreator.JWTCreatorComponent;
import com.gymer.commoncomponents.languagepack.LanguageComponent;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
@RequiredArgsConstructor
class CustomLoginSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final JWTCreatorComponent jwtCreatorComponent;
    private final LanguageComponent language;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {
        final boolean IS_ERROR = false;
        response.setHeader("Authorization", jwtCreatorComponent.createToken(authentication));
        response.setStatus(HttpStatus.OK.value());
        response.setContentType("application/json");
        response.getWriter().write(objectMapper.writeValueAsString(new HandlerResponse(language.successfullyLoggedIn(), IS_ERROR)));
        response.getWriter().flush();
        clearAuthenticationAttributes(request);
    }

}
