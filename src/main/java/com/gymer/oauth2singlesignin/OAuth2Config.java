package com.gymer.oauth2singlesignin;

import lombok.RequiredArgsConstructor;
import org.springframework.core.env.Environment;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OAuth2Config {

    private final Environment environment;
    private final OAuth2SuccessHandler oAuth2SuccessHandler;
    private final OAuth2FailureHandler oAuth2FailureHandler;

    public void configureOAuth2(HttpSecurity http) throws Exception {
        http.oauth2Login()
                .loginPage(environment.getProperty("server.address.frontend") + "/login")
                .successHandler(oAuth2SuccessHandler)
                .failureHandler(oAuth2FailureHandler);
    }

}
