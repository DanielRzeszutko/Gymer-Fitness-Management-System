package com.gymer.components.security.common.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gymer.components.common.entity.JsonResponse;
import com.gymer.components.common.entity.Response;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class JsonLogoutSuccessHandler implements LogoutSuccessHandler {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void onLogoutSuccess(HttpServletRequest httpServletRequest, HttpServletResponse response, Authentication authentication) throws IOException {
        JsonResponse message = JsonResponse.validMessage("Successfully logged out.");

        response.setHeader("Authorization", "");
        response.setStatus(HttpStatus.OK.value());
        response.setContentType("application/json");
        response.getWriter().write(objectMapper.writeValueAsString(message));
        response.getWriter().flush();
    }

}
