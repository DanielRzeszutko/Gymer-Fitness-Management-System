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
import com.google.api.services.calendar.model.EventAttendee;
import com.google.api.services.calendar.model.EventDateTime;
import com.google.api.services.calendar.model.EventReminder;
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
import java.sql.Date;
import java.sql.Time;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@RestController
@RequiredArgsConstructor
class SynchronizeSlotWithGoogleService {

    private final AccountOwnerValidator accountOwnerValidator;

    private final HttpSession session;
    private final OAuth2AuthorizedClientService authClientService;
    private final LanguageComponent language;

    private static final String APPLICATION_NAME = "Google Calendar API Gymer";
    private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();
    private static final List<String> SCOPES = Collections.singletonList(CalendarScopes.CALENDAR);

    public void synchronizeSlotWithGoogleCalendar(Partner partner, Slot slot) {
        if (!session.getAttributeNames().hasMoreElements()) return;

        try {
            NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();

            OAuth2AuthenticationToken oauthToken = (OAuth2AuthenticationToken) session.getAttribute("userToken");
            if (oauthToken == null)
                throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, language.userNotLoggedViaSingleSignIn());

            OAuth2AccessToken authAccessToken = getOAuth2AccessToken(oauthToken);
            GoogleCredential credentialDetails = getGoogleCredential(HTTP_TRANSPORT, authAccessToken);
            Calendar service = getCalendarServiceFromCredentials(HTTP_TRANSPORT, credentialDetails);

            Event event = createEvent(partner, slot);
            service.events().insert("primary", event).execute();
        }
        catch (GeneralSecurityException | IOException e) {
            System.out.println(e);
        }
    }

    private OAuth2AccessToken getOAuth2AccessToken(OAuth2AuthenticationToken oauthToken) {
        OAuth2AuthorizedClient authClient = authClientService.loadAuthorizedClient(
                oauthToken.getAuthorizedClientRegistrationId(),
                oauthToken.getName());
        return authClient.getAccessToken();
    }

    private Calendar getCalendarServiceFromCredentials(NetHttpTransport HTTP_TRANSPORT, GoogleCredential credentialDetails) {
        return new Calendar.Builder(HTTP_TRANSPORT, JSON_FACTORY, credentialDetails)
                .setApplicationName(APPLICATION_NAME)
                .build();
    }

    private GoogleCredential getGoogleCredential(NetHttpTransport HTTP_TRANSPORT, OAuth2AccessToken authAccessToken) throws IOException {
        InputStream resource = SynchronizeSlotWithGoogleService.class
                .getClassLoader()
                .getResourceAsStream("credentials.json");
        assert resource != null;
        GoogleCredential credentialDetails = GoogleCredential.fromStream(resource, HTTP_TRANSPORT, JSON_FACTORY).createScoped(SCOPES);
        credentialDetails.setAccessToken(authAccessToken.getTokenValue());
        return credentialDetails;
    }

    public boolean isUserLoggedAsActiveUser(Long userId) {
        return accountOwnerValidator.isOwnerLoggedIn(userId);
    }

    private Event createEvent(Partner partner, Slot slot) {
        Event event = new Event()
                .setSummary(slot.getSlotType())
                .setLocation(partner.getAddress().toString())
                .setDescription(slot.getDescription());
        configureEvent(partner, slot, event);
        return event;
    }

    private void configureEvent(Partner partner, Slot slot, Event event) {
        setDateToEvent(slot, event);
        setEmployeeToEvent(partner, slot, event);
        setRemindersToEvent(event);
    }

    private void setRemindersToEvent(Event event) {
        EventReminder[] reminderOverrides = new EventReminder[]{
                new EventReminder().setMethod("email").setMinutes(24 * 60),
                new EventReminder().setMethod("popup").setMinutes(60),
        };
        Event.Reminders reminders = new Event.Reminders()
                .setUseDefault(false)
                .setOverrides(Arrays.asList(reminderOverrides));

        event.setReminders(reminders);
    }

    private void setEmployeeToEvent(Partner partner, Slot slot, Event event) {
        String employeeName = getEmployeeNameFromSlot(partner, slot);
        EventAttendee employee = new EventAttendee()
                .setEmail(partner.getCredential().getEmail())
                .setDisplayName(employeeName)
                .setComment(partner.getName() + " - " + partner.getCredential().getEmail());

        event.setAttendees(Collections.singletonList(employee));
    }

    private String getEmployeeNameFromSlot(Partner partner, Slot slot) {
        return slot.getEmployee() == null
                ? partner.getName()
                : slot.getEmployee().getFirstName() + " " + slot.getEmployee().getLastName();
    }

    private void setDateToEvent(Slot slot, Event event) {
        EventDateTime start = getEventDateTime(slot.getDate(), slot.getStartTime());
        EventDateTime end = getEventDateTime(slot.getDate(), slot.getEndTime());

        event.setStart(start);
        event.setEnd(end);
    }

    private EventDateTime getEventDateTime(Date date, Time time) {
        DateTime dateTime = new DateTime(date.toString() + "T" + time + "+01:00");
        return new EventDateTime().setDateTime(dateTime);
    }

}
