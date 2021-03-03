package com.gymer.common.jwtcreator;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import lombok.RequiredArgsConstructor;
import org.springframework.core.env.Environment;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
@RequiredArgsConstructor
public class JWTCreatorComponent {

    private final Environment environment;

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

}
