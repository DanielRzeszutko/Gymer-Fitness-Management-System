package com.gymer.components.security.common.handler;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gymer.components.common.entity.JsonResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Date;

@Component
public class LoginSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    final String TOKEN_HEADER_NAME = "Authorization";
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final Environment environment;

    @Autowired
    public LoginSuccessHandler(Environment environment) {
        this.environment = environment;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {
        response.setHeader(TOKEN_HEADER_NAME, createToken(authentication));
        response.setStatus(HttpStatus.OK.value());
        response.setContentType("application/json");

        JsonResponse message = new JsonResponse("Successfully logged in.", false);
        response.getWriter().write(objectMapper.writeValueAsString(message));
        response.getWriter().flush();
        clearAuthenticationAttributes(request);
    }

    private String createToken(Authentication authentication) {
        String starter = "Bearer ";
        String secretKey = environment.getProperty("jwt.secret.password");
        secretKey = secretKey != null ? secretKey : "";
        Algorithm algorithm = Algorithm.HMAC512(secretKey);
        return createNewJwt(authentication, starter, algorithm);
    }

    private String createNewJwt(Authentication authentication, String tokenStarter, Algorithm algorithm) {
        long now = System.currentTimeMillis();
        return tokenStarter + JWT.create()
                .withSubject(authentication.getName())
                .withClaim("roles", String.valueOf(authentication.getAuthorities()))
                .withIssuedAt(new Date(now))
                .withExpiresAt(new Date(now + 864000000))
                .sign(algorithm);
    }

}
