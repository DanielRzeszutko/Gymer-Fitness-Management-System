package com.gymer.commoncomponents.jwtcreator;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.core.env.Environment;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.Base64;
import java.util.Date;

@Component
@RequiredArgsConstructor
public class JWTCreatorComponent {

    private final Environment environment;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public String createToken(Authentication authentication) {
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

    public String createOAuth2Token(OAuth2AuthenticationToken authentication) {
        String secretKey = environment.getProperty("jwt.secret.password");
        secretKey = secretKey != null ? secretKey : "";
        Algorithm algorithm = Algorithm.HMAC512(secretKey);
        long now = System.currentTimeMillis();

        try {
            ByteArrayOutputStream bo = new ByteArrayOutputStream();
            ObjectOutputStream so = new ObjectOutputStream(bo);
            so.writeObject(authentication);
            so.close();
            String serializableAuthentication = Base64.getEncoder().encodeToString(bo.toByteArray());

            return JWT.create()
                    .withSubject(serializableAuthentication)
                    .withIssuedAt(new Date(now))
                    .withExpiresAt(new Date(now + 864000000))
                    .sign(algorithm);
        } catch (IOException e) {
            return null;
        }
    }

}
