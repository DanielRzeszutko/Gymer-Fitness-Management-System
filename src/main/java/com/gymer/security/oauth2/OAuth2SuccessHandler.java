package com.gymer.security.oauth2;

import com.gymer.common.crudresources.address.entity.Address;
import com.gymer.common.crudresources.credential.CredentialService;
import com.gymer.common.crudresources.credential.entity.Credential;
import com.gymer.common.crudresources.credential.entity.Role;
import com.gymer.common.crudresources.partner.entity.Partner;
import com.gymer.common.crudresources.user.UserService;
import com.gymer.common.crudresources.user.entity.User;
import com.gymer.security.login.LoginSuccessHandler;
import com.gymer.security.register.entity.RegistrationDetails;
import lombok.AllArgsConstructor;
import net.bytebuddy.utility.RandomString;
import org.springframework.core.env.Environment;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.Collections;

@Component
@AllArgsConstructor
public class OAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final LoginSuccessHandler successHandler;
    private final CredentialService credentialService;
    private final UserService userService;
    private final Environment environment;
    private final RedirectStrategy redirectStrategy = new DefaultRedirectStrategy();

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {
        OAuth2AuthenticationToken token = (OAuth2AuthenticationToken) authentication;
        String userEmail = token.getPrincipal().getAttribute("email");
        String providerId = token.getPrincipal().getAttribute("sub");

        User userByProvider = userService.findByProviderId(providerId);
        if (userByProvider != null) {
            continueSettingAuthentication(request, response, userEmail);
            return;
        }

        if (userService.isUserExistsByEmail(userEmail)) {
            Credential credential = credentialService.getCredentialByEmail(userEmail);
            User userByEmail = userService.getByCredentials(credential);
            userByEmail.setProviderId(providerId);
            userService.updateElement(userByEmail);
            continueSettingAuthentication(request, response, userEmail);
            return;
        }

        User newUser = createNewUser(userEmail, providerId);
        userService.updateElement(newUser);
        continueSettingAuthentication(request, response, userEmail);
    }

    private void continueSettingAuthentication(HttpServletRequest request, HttpServletResponse response, String userEmail) throws IOException {
        GrantedAuthority authority = new SimpleGrantedAuthority("ROLE_USER");
        Authentication authentication = new UsernamePasswordAuthenticationToken(userEmail, null, Collections.singletonList(authority));
        SecurityContextHolder.getContext().setAuthentication(authentication);

        String redirectUrl = environment.getProperty("server.address.frontend") + "/google";
        redirectStrategy.sendRedirect(request, response, redirectUrl);
        successHandler.onAuthenticationSuccess(request, response, authentication);
    }

    private User createNewUser(String userEmail, String providerId) {
        Credential credential = createCredentialBy(userEmail);
        User user = new User("", "", credential);
        user.setProviderId(providerId);
        return user;
    }

    private Credential createCredentialBy(String userEmail) {
        Timestamp timestamp = new Timestamp(new java.util.Date().getTime());
        return new Credential(userEmail, null, "", Role.USER, true, timestamp);
    }

}
