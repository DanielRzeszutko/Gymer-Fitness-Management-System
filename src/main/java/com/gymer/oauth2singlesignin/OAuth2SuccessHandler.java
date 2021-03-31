package com.gymer.oauth2singlesignin;

import com.gymer.commoncomponents.googlecalendar.GoogleCalendarOperationService;
import com.gymer.commoncomponents.languagepack.LanguageComponent;
import com.gymer.commonresources.authorize.OAuth2AuthorizeService;
import com.gymer.commonresources.credential.CredentialService;
import com.gymer.commonresources.credential.entity.Credential;
import com.gymer.commonresources.credential.entity.Role;
import com.gymer.commonresources.partner.PartnerService;
import com.gymer.commonresources.slot.SlotService;
import com.gymer.commonresources.slot.entity.Slot;
import com.gymer.commonresources.user.UserService;
import com.gymer.commonresources.user.entity.User;
import lombok.AllArgsConstructor;
import org.springframework.core.env.Environment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
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
class OAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final CredentialService credentialService;
    private final UserService userService;
    private final PartnerService partnerService;
    private final Environment environment;
    private final LanguageComponent language;
    private final OAuth2AuthorizeService authorizeService;
    private final SlotService slotService;
    private final GoogleCalendarOperationService operationService;
    private final RedirectStrategy redirectStrategy = new DefaultRedirectStrategy();

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {
        OAuth2AuthenticationToken token = (OAuth2AuthenticationToken) authentication;
        String userEmail = token.getPrincipal().getAttribute("email");
        String providerId = token.getPrincipal().getAttribute("sub");

        // if user in database with google account exists
        if (userService.findByProviderId(providerId) != null) {
            continueSettingAuthentication(request, response, token);
            return;
        }

        // if partner with same email address exists in database
        if (partnerService.isUserExistsByEmail(userEmail)) {
            String errorMessage = language.cannotSignInViaOAuth2BecauseOfBeingPartner();
            String redirectUrl = environment.getProperty("server.address.frontend") + "/login?error=" + errorMessage;
            redirectStrategy.sendRedirect(request, response, redirectUrl);
            SecurityContextHolder.getContext().setAuthentication(null);
            clearAuthenticationAttributes(request);
            return;
        }

        // if user with same email address exists in database
        if (userService.isUserExistsByEmail(userEmail)) {
            Credential credential = credentialService.getCredentialByEmail(userEmail);
            User user = userService.getByCredentials(credential);
            Page<Slot> slots = slotService.findAllSlotsForUser(Pageable.unpaged(), user);
            user.setProviderId(providerId);

            operationService.insertAllEvents(slots.toList(), token);
            continueSettingAuthentication(request, response, token);
            return;
        }

        // if user not exists
        User newUser = createNewUser(token);
        userService.updateElement(newUser);
        continueSettingAuthentication(request, response, token);
    }

    private void continueSettingAuthentication(HttpServletRequest request, HttpServletResponse response, OAuth2AuthenticationToken token) throws IOException {
        GrantedAuthority authority = new SimpleGrantedAuthority("ROLE_USER");
        String userEmail = token.getPrincipal().getAttribute("email");

        Authentication authentication = new UsernamePasswordAuthenticationToken(userEmail, null, Collections.singletonList(authority));
        SecurityContextHolder.getContext().setAuthentication(authentication);

        Credential credential = credentialService.getCredentialByEmail(userEmail);
        User user = userService.getByCredentials(credential);
        authorizeService.saveAuthorizationObject(user, token);

        String redirectUrl = environment.getProperty("server.address.frontend") + "/google";
        redirectStrategy.sendRedirect(request, response, redirectUrl);
    }

    private User createNewUser(OAuth2AuthenticationToken token) {
        OAuth2User attributes = token.getPrincipal();
        String email = attributes.getAttribute("email");
        String providerId = attributes.getAttribute("sub");
        String name = attributes.getAttribute("given_name");
        String surname = attributes.getAttribute("family_name");
        Credential credential = createCredentialBy(email);
        User user = new User(name, surname, credential);
        user.setProviderId(providerId);
        return user;
    }

    private Credential createCredentialBy(String userEmail) {
        Timestamp timestamp = new Timestamp(new java.util.Date().getTime());
        return new Credential(userEmail, null, "", Role.USER, true, timestamp);
    }

}
