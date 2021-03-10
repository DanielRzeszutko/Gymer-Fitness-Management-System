package com.gymer.googlecalendar;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.CalendarScopes;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.Events;
import lombok.RequiredArgsConstructor;
import org.apache.tomcat.util.http.fileupload.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.annotation.RegisteredOAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.OAuth2RefreshToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.security.GeneralSecurityException;
import java.util.Collections;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class GoogleCalendarController {

    private final HttpSession session;
    private final OAuth2AuthorizedClientService clientService;

    private static final String APPLICATION_NAME = "Google Calendar API Java Quickstart";
    private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();
    private static final List<String> SCOPES = Collections.singletonList(CalendarScopes.CALENDAR);

    @GetMapping("/api/google-active")
    public void getGoogleAccount() throws IOException, GeneralSecurityException {
        NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();

        InputStream resource = GoogleCalendarController.class.getClassLoader().getResourceAsStream("credentials.json");
        GoogleCredential credentialDetails = GoogleCredential.fromStream(resource, HTTP_TRANSPORT, JSON_FACTORY).createScoped(SCOPES);

        OAuth2AuthenticationToken oauthToken = (OAuth2AuthenticationToken) session.getAttribute("userToken");
        String clientRegistrationId = oauthToken.getAuthorizedClientRegistrationId();
        if (clientRegistrationId.equals("google")) {
            OAuth2AuthorizedClient client = clientService.loadAuthorizedClient(clientRegistrationId, oauthToken.getName());
            System.out.println(client.getRefreshToken());
        }

        GoogleCredential credential = new GoogleCredential.Builder()
                .setTransport(credentialDetails.getTransport())
                .setJsonFactory(credentialDetails.getJsonFactory())
                .setServiceAccountId(credentialDetails.getServiceAccountId())
                .setServiceAccountUser("r2y.szczepaniak@gmail.com")
                .setServiceAccountPrivateKey(credentialDetails.getServiceAccountPrivateKey())
                .setServiceAccountScopes(credentialDetails.getServiceAccountScopes())
                .build();

        Calendar service = new Calendar.Builder(HTTP_TRANSPORT, JSON_FACTORY, credential)
                .setApplicationName(APPLICATION_NAME)
                .build();

        DateTime now = new DateTime(System.currentTimeMillis());
        Events events = service.events().list("primary")
                .setMaxResults(10)
                .setTimeMin(now)
                .setOrderBy("startTime")
                .setSingleEvents(true)
                .execute();
        List<Event> items = events.getItems();
        if (items.isEmpty()) {
            System.out.println("No upcoming events found.");
        } else {
            System.out.println("Upcoming events");
            for (Event event : items) {
                DateTime start = event.getStart().getDateTime();
                if (start == null) {
                    start = event.getStart().getDate();
                }
                System.out.printf("%s (%s)\n", event.getSummary(), start);

            }

        }
    }
}
