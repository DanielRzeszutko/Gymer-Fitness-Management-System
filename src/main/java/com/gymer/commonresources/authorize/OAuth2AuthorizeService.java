package com.gymer.commonresources.authorize;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.interfaces.JWTVerifier;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gymer.commoncomponents.jwtcreator.JWTCreatorComponent;
import com.gymer.commonresources.user.entity.User;
import javassist.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.core.env.Environment;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.Base64;

@Service
@RequiredArgsConstructor
public class OAuth2AuthorizeService {

    private final Environment environment;
    private final JWTCreatorComponent jwtCreator;
    private final OAuth2AuthorizeRepository repository;

    public void saveAuthorizationObject(User user, OAuth2AuthenticationToken authorization) {
        String authorizationToken = jwtCreator.createOAuth2Token(authorization);
        OAuth2AuthorizeEntity authorizationEntity = repository.findByUserId(user.getId()).orElse(
                new OAuth2AuthorizeEntity(user, null)
        );
        authorizationEntity.setKey(authorizationToken);
        repository.save(authorizationEntity);
    }

    public OAuth2AuthenticationToken getAuthorizationObject(Long userId) throws NotFoundException {
        OAuth2AuthorizeEntity authorizationEntity = repository.findByUserId(userId).orElseThrow(
                () -> new NotFoundException("OAuth2 Authorization object not found in database.")
        );
        String jwtSecretKey = environment.getProperty("jwt.secret.password");
        jwtSecretKey = jwtSecretKey != null ? jwtSecretKey : "";
        JWTVerifier verifier = JWT.require(Algorithm.HMAC512(jwtSecretKey)).build();

        String token = authorizationEntity.getKey();
        DecodedJWT verifiedJWT = verifier.verify(token);

        try {
            byte [] data = Base64.getDecoder().decode(verifiedJWT.getSubject());
            ObjectInputStream ois = new ObjectInputStream(
                    new ByteArrayInputStream(data)
            );
            OAuth2AuthenticationToken object = (OAuth2AuthenticationToken) ois.readObject();
            ois.close();
            return object;
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            throw new NotFoundException("OAuth2 Authorization object cannot be read from database.");
        }
    }

}
