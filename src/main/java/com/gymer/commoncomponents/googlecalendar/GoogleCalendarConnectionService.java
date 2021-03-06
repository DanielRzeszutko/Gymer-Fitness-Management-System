package com.gymer.commoncomponents.googlecalendar;

import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.CalendarScopes;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.security.GeneralSecurityException;
import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
class GoogleCalendarConnectionService {

    private static final String APPLICATION_NAME = "Google Calendar API Gymer";
    private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();
    private static final List<String> SCOPES = Collections.singletonList(CalendarScopes.CALENDAR);
    private final OAuth2AuthorizedClientService authClientService;

    public Calendar connectToGoogleCalendar(OAuth2AuthenticationToken oauthToken) throws GeneralSecurityException, IOException {
        NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();

        OAuth2AccessToken authAccessToken = getOAuth2AccessToken(oauthToken);
        GoogleCredential credentialDetails = getGoogleCredential(HTTP_TRANSPORT, authAccessToken);
        return getCalendarFromCredentials(HTTP_TRANSPORT, credentialDetails);
    }

    private OAuth2AccessToken getOAuth2AccessToken(OAuth2AuthenticationToken oauthToken) {
        OAuth2AuthorizedClient authClient = authClientService.loadAuthorizedClient(
                oauthToken.getAuthorizedClientRegistrationId(),
                oauthToken.getName());
        return authClient.getAccessToken();
    }

    private GoogleCredential getGoogleCredential(NetHttpTransport HTTP_TRANSPORT, OAuth2AccessToken authAccessToken) throws IOException {
        InputStream resource = GoogleCalendarConnectionService.class
                .getClassLoader()
                .getResourceAsStream("credentials.json");
        assert resource != null;
        GoogleCredential credentialDetails = GoogleCredential.fromStream(resource, HTTP_TRANSPORT, JSON_FACTORY).createScoped(SCOPES);
        credentialDetails.setAccessToken(authAccessToken.getTokenValue());
        return credentialDetails;
    }

    private Calendar getCalendarFromCredentials(NetHttpTransport HTTP_TRANSPORT, GoogleCredential credentialDetails) {
        return new Calendar.Builder(HTTP_TRANSPORT, JSON_FACTORY, credentialDetails)
                .setApplicationName(APPLICATION_NAME)
                .build();
    }

}
