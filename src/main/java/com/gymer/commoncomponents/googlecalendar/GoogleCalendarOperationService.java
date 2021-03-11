package com.gymer.commoncomponents.googlecalendar;

import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.EventAttendee;
import com.google.api.services.calendar.model.EventDateTime;
import com.google.api.services.calendar.model.EventReminder;
import com.gymer.commoncomponents.languagepack.LanguageComponent;
import com.gymer.commonresources.partner.PartnerService;
import com.gymer.commonresources.partner.entity.Partner;
import com.gymer.commonresources.slot.SlotService;
import com.gymer.commonresources.slot.entity.Slot;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.sql.Date;
import java.sql.Time;
import java.util.Arrays;

@Service
@RequiredArgsConstructor
public class GoogleCalendarOperationService {

    private final HttpSession session;
    private final LanguageComponent language;

    private final PartnerService partnerService;
    private final GoogleCalendarConnectionService connectionService;

    public void manipulateWithEvent(Slot slot, CalendarOperation operation) {
        OAuth2AuthenticationToken oauthToken = (OAuth2AuthenticationToken) session.getAttribute("userToken");
        if (!session.getAttributeNames().hasMoreElements() || oauthToken == null)
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, language.userNotLoggedViaSingleSignIn());

        try {
            Calendar calendar = connectionService.connectToGoogleCalendar(oauthToken);
            System.out.println("Kalendarz jest");
            Partner partner = partnerService.findPartnerContainingSlot(slot);
            System.out.println("Partner jest");

            switch (operation) {
                case INSERT -> insertNewEvent(calendar, partner, slot);
                case MODIFY -> updateOldEvent(calendar, partner, slot);
                case REMOVE -> removeOldEvent(calendar, slot);
            }
        } catch (IOException | GeneralSecurityException e) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, language.thereIsNoValidCalendar());
        }

    }

    private void insertNewEvent(Calendar calendar, Partner partner, Slot slot) throws IOException {
        Event event = createEvent(partner, slot);
        event.setId("agmc" + slot.getId());
        calendar.events().insert("primary", event).execute();
    }

    private void updateOldEvent(Calendar calendar, Partner partner, Slot slot) throws IOException {
        Event event = createEvent(partner, slot);
        calendar.events().update("primary", "agmc" + slot.getId(), event).execute();
    }

    private void removeOldEvent(Calendar calendar, Slot slot) throws IOException {
        calendar.events().delete("primary", "agmc" + slot.getId()).execute();
    }

    private Event createEvent(Partner partner, Slot slot) {
        Event event = new Event()
                .setSummary(slot.getSlotType() + " " + slot.getDescription())
                .setLocation(partner.getAddress().toString())
                .setDescription(getEmployeeNameFromSlot(partner, slot));
        configureEvent(slot, event);
        return event;
    }

    private void configureEvent(Slot slot, Event event) {
        setDateToEvent(slot, event);
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
