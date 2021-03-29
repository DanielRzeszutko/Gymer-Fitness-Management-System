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

@Service
@RequiredArgsConstructor
public class OAuth2AuthorizeService {

    private final Environment environment;
    private final JWTCreatorComponent jwtCreator;
    private final OAuth2AuthorizeRepository repository;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public void saveAuthorizationObject(User user, OAuth2AuthenticationToken authorization) throws JsonProcessingException {
        String authorizationToken = jwtCreator.createOAuth2Token(authorization);
        OAuth2AuthorizeEntity authorizationEntity = repository.findByUser(user).orElse(
                new OAuth2AuthorizeEntity(user, null)
        );
        authorizationEntity.setKey(authorizationToken);
        repository.save(authorizationEntity);
    }

    public OAuth2AuthenticationToken getAuthorizationObject(User user) throws NotFoundException, JsonProcessingException {
        OAuth2AuthorizeEntity authorizationEntity = repository.findByUser(user).orElseThrow(
                () -> new NotFoundException("OAuth2 Authorization object not found in database.")
        );
        String jwtSecretKey = environment.getProperty("jwt.secret.password");
        jwtSecretKey = jwtSecretKey != null ? jwtSecretKey : "";
        JWTVerifier verifier = JWT.require(Algorithm.HMAC512(jwtSecretKey)).build();

        String token = authorizationEntity.getKey();
        DecodedJWT verifiedJWT = verifier.verify(token);

        return objectMapper.readValue(verifiedJWT.getSubject(), OAuth2AuthenticationToken.class);
    }

}
