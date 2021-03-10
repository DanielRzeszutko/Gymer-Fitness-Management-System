package com.gymer.synchronizeslotwithgooglecalendar;

import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.CalendarScopes;
import com.google.api.services.calendar.model.*;
import com.gymer.commoncomponents.accountvalidator.AccountOwnerValidator;
import com.gymer.commoncomponents.languagepack.LanguageComponent;
import com.gymer.commonresources.partner.entity.Partner;
import com.gymer.commonresources.slot.entity.Slot;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.InputStream;
import java.security.GeneralSecurityException;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

@RestController
@RequiredArgsConstructor
class SynchronizeSlotWithGoogleService {

    private final AccountOwnerValidator accountOwnerValidator;

    private final HttpSession session;
    private final OAuth2AuthorizedClientService authClientService;
    private final LanguageComponent language;

    private static final String APPLICATION_NAME = "Google Calendar API Java Quickstart";
    private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();
    private static final List<String> SCOPES = Collections.singletonList(CalendarScopes.CALENDAR);

    public void synchronizeSlotWithGoogleCalendar(Partner partner, Slot slot) {
        if (!session.getAttributeNames().hasMoreElements()) return;

        try {
            NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();

            OAuth2AuthenticationToken oauthToken = (OAuth2AuthenticationToken) session.getAttribute("userToken");
            if (oauthToken == null) throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, language.userNotLoggedViaSingleSignIn());

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

            Event event = new Event()
                    .setSummary(slot.getSlotType())
                    .setLocation(partner.getAddress().toString())
                    .setDescription(slot.getDescription());

            String date = slot.getDate().toString();
            String startTime = slot.getStartTime().toString();
            String endTime = slot.getEndTime().toString();

            DateTime startDateTime = new DateTime(date + "T" + startTime + "+01:00");
            EventDateTime start = new EventDateTime()
                    .setDateTime(startDateTime);
            event.setStart(start);

            DateTime endDateTime = new DateTime(date + "T" + endTime + "+01:00");
            EventDateTime end = new EventDateTime()
                    .setDateTime(endDateTime);
            event.setEnd(end);

            String employeeName = slot.getEmployee() == null
                    ? partner.getName()
                    : slot.getEmployee().getFirstName() + " " + slot.getEmployee().getLastName();
            EventAttendee employee = new EventAttendee()
                    .setEmail(partner.getCredential().getEmail())
                    .setDisplayName(employeeName)
                    .setComment(partner.getName() + " - " + partner.getCredential().getEmail());

            event.setAttendees(Collections.singletonList(employee));

            EventReminder[] reminderOverrides = new EventReminder[] {
                    new EventReminder().setMethod("email").setMinutes(24 * 60),
                    new EventReminder().setMethod("popup").setMinutes(60),
            };
            Event.Reminders reminders = new Event.Reminders()
                    .setUseDefault(false)
                    .setOverrides(Arrays.asList(reminderOverrides));
            event.setReminders(reminders);

            service.events().insert("primary", event).execute();
        } catch (GeneralSecurityException | IOException e) {
            System.out.println(e);
        }

    }

    public boolean isUserLoggedAsActiveUser(Long userId) {
        return accountOwnerValidator.isOwnerLoggedIn(userId);
    }

}
