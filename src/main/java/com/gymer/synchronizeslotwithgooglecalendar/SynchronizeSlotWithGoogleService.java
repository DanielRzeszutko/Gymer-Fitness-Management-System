package com.gymer.synchronizeslotwithgooglecalendar;

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
import com.gymer.commoncomponents.accountvalidator.AccountOwnerValidator;
import com.gymer.commonresources.slot.entity.Slot;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.InputStream;
import java.security.GeneralSecurityException;
import java.util.Collections;
import java.util.List;

@RestController
@RequiredArgsConstructor
class SynchronizeSlotWithGoogleService {

    private final AccountOwnerValidator accountOwnerValidator;

    private final HttpSession session;
    private final OAuth2AuthorizedClientService authClientService;

    private static final String APPLICATION_NAME = "Google Calendar API Java Quickstart";
    private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();
    private static final List<String> SCOPES = Collections.singletonList(CalendarScopes.CALENDAR);

    public void synchronizeSlotWithGoogleCalendar(Slot slot) {
        if (!session.getAttributeNames().hasMoreElements()) return;

        try {
            NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();

            OAuth2AuthenticationToken oauthToken = (OAuth2AuthenticationToken) session.getAttribute("userToken");
            OAuth2AuthorizedClient authClient = authClientService.loadAuthorizedClient(
                    oauthToken.getAuthorizedClientRegistrationId(),
                    oauthToken.getName());
            OAuth2AccessToken authAccessToken = authClient.getAccessToken();

            InputStream resource = SynchronizeSlotWithGoogleService.class.getClassLoader().getResourceAsStream("credentials.json");
            GoogleCredential credentialDetails = GoogleCredential.fromStream(resource, HTTP_TRANSPORT, JSON_FACTORY).createScoped(SCOPES);
            credentialDetails.setAccessToken(authAccessToken.getTokenValue());

            Calendar service = new Calendar.Builder(HTTP_TRANSPORT, JSON_FACTORY, credentialDetails)
                    .setApplicationName(APPLICATION_NAME)
                    .build();

            /*
             * Instead of showing details there need to be fragment
             * responsible for adding slot or update slot in google calendar
             */
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
        } catch (GeneralSecurityException | IOException e) {
            System.out.println(e);
        }

    }

    public boolean isUserLoggedAsActiveUser(Long userId) {
        return accountOwnerValidator.isOwnerLoggedIn(userId);
    }

}
