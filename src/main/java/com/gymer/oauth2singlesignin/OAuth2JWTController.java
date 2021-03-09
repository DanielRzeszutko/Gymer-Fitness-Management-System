package com.gymer.oauth2singlesignin;

import com.google.api.client.auth.oauth2.AuthorizationCodeRequestUrl;
import com.google.api.client.auth.oauth2.TokenResponse;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.CalendarScopes;
import com.gymer.commoncomponents.jwtcreator.JWTCreatorComponent;
import com.gymer.commoncomponents.languagepack.LanguageComponent;
import com.gymer.commonresources.credential.CredentialService;
import com.gymer.commonresources.credential.entity.Credential;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.view.RedirectView;

import javax.servlet.http.HttpServletResponse;
import java.util.Collections;
import java.util.List;

@RestController
@RequiredArgsConstructor
class OAuth2JWTController {

    private static final String APPLICATION_NAME = "Gymer";
    private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();
    private static final String TOKENS_DIRECTORY_PATH = "tokens";
    private static final List<String> SCOPES = Collections.singletonList(CalendarScopes.CALENDAR);
    private static final String CREDENTIALS_FILE_PATH = "/credentials.json";
    private static NetHttpTransport HTTP_TRANSPORT;

    private final CredentialService credentialService;
    private final JWTCreatorComponent jwtCreatorComponent;
    private final LanguageComponent language;
    private final RedirectStrategy redirectStrategy = new DefaultRedirectStrategy();

    GoogleClientSecrets clientSecrets;
    GoogleAuthorizationCodeFlow flow;
    com.google.api.client.auth.oauth2.Credential credential;

    @Value("${google.client.client-id}")
    private String clientId;
    @Value("${google.client.client-secret}")
    private String clientSecret;
    @Value("${google.client.redirectUri}")
    private String redirectURI;

    @GetMapping("/api/google")
    public void obtainJWTIfLoggedByGoogle(HttpServletResponse response, Authentication authentication) {
        if (authentication == null || authentication.getPrincipal() == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, language.userNotLoggedViaSingleSignIn());
        }

        String userEmail = (String) authentication.getPrincipal();
        Credential credential = credentialService.getCredentialByEmail(userEmail);
        GrantedAuthority authority = new SimpleGrantedAuthority("ROLE_" + credential.getRole());
        authentication = new UsernamePasswordAuthenticationToken(userEmail, null, Collections.singletonList(authority));
        response.setHeader("Authorization", jwtCreatorComponent.createToken(authentication));
        response.setStatus(HttpStatus.OK.value());
        response.setContentType("application/json");
    }

    @GetMapping("/api/google-auth")
    public Object oAuth2Callback(@RequestParam(required = false) String code) throws Exception {
        if (code == null) {
            return new RedirectView(authorize());
        }
        String message = "";
        try {

            TokenResponse response = flow.newTokenRequest(code).setRedirectUri("http://localhost:8080/login/oauth2/code/google").execute();
            com.google.api.client.auth.oauth2.Credential credential = flow.createAndStoreCredential(response, "userID");
            Calendar client = new Calendar.Builder(HTTP_TRANSPORT, JSON_FACTORY, credential)
                    .setApplicationName(APPLICATION_NAME).build();

        } catch (Exception e) {
            message = "Exception while handling OAuth2 callback (" + e.getMessage() + ")."
                    + " Redirecting to google connection status page.";
        }
        System.out.println("cal message:" + message);
        return new ResponseEntity<>(message, HttpStatus.OK);
    }

    private String authorize() throws Exception {
        AuthorizationCodeRequestUrl authorizationUrl;
        if (flow == null) {
            GoogleClientSecrets.Details web = new GoogleClientSecrets.Details();
            web.setClientId(clientId);
            web.setClientSecret(clientSecret);
            clientSecrets = new GoogleClientSecrets().setWeb(web);
            HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
            flow = new GoogleAuthorizationCodeFlow.Builder(HTTP_TRANSPORT, JSON_FACTORY, clientSecrets,
                    Collections.singleton(CalendarScopes.CALENDAR)).build();
        }
        authorizationUrl = flow.newAuthorizationUrl().setRedirectUri(redirectURI);
        System.out.println("cal authorizationUrl->" + authorizationUrl);

        return authorizationUrl.build();
    }

}
