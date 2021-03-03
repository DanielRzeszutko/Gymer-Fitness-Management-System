package com.gymer.security.login;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gymer.common.crudresources.credential.entity.Credential;
import com.gymer.common.entity.JsonResponse;
import com.gymer.security.common.components.JWTCreatorComponent;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collections;

@Component
@RequiredArgsConstructor
public class LoginSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final JWTCreatorComponent jwtCreatorComponent;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {
        response.setHeader("Authorization", jwtCreatorComponent.createToken(authentication));
        response.setStatus(HttpStatus.OK.value());
        response.setContentType("application/json");

        JsonResponse message = JsonResponse.validMessage("Successfully logged in.");
        response.getWriter().write(objectMapper.writeValueAsString(message));
        response.getWriter().flush();

        clearAuthenticationAttributes(request);
    }

}
