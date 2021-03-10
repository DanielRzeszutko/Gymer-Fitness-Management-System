package com.gymer.accountlogin;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gymer.commoncomponents.languagepack.LanguageComponent;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

@Component
@RequiredArgsConstructor
class CustomLogoutSuccessHandler implements LogoutSuccessHandler {

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final HttpSession session;
    private final LanguageComponent language;

    @Override
    public void onLogoutSuccess(HttpServletRequest httpServletRequest, HttpServletResponse response, Authentication authentication) throws IOException {
        final boolean IS_ERROR = false;
        session.setAttribute("userToken", null);
        SecurityContextHolder.getContext().setAuthentication(null);
        response.setStatus(HttpStatus.OK.value());
        response.setContentType("application/json");
        response.getWriter().write(objectMapper.writeValueAsString(new HandlerResponse(language.successfullyLoggedOut(), IS_ERROR)));
        response.getWriter().flush();
    }

}
