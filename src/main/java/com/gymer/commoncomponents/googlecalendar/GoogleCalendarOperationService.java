package com.gymer.commoncomponents.googlecalendar;

import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.EventDateTime;
import com.google.api.services.calendar.model.EventReminder;
import com.gymer.commoncomponents.languagepack.LanguageComponent;
import com.gymer.commonresources.authorize.OAuth2AuthorizeService;
import com.gymer.commonresources.partner.PartnerService;
import com.gymer.commonresources.partner.entity.Partner;
import com.gymer.commonresources.slot.entity.Slot;
import com.gymer.commonresources.user.entity.User;
import javassist.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.sql.Date;
import java.sql.Time;
import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
public class GoogleCalendarOperationService {

    private final OAuth2AuthorizeService authorizeService;
    private final LanguageComponent language;

    private final String EVENT_NAME_ADDON = "agmc";

    private final PartnerService partnerService;
    private final GoogleCalendarConnectionService connectionService;

    public void manipulateWithEvent(Slot slot, User user, CalendarOperation operation) {
        try {
            OAuth2AuthenticationToken oauthToken = checkIfUserIsLoggedByOAuth2(user);
            if (oauthToken == null) return;

            Calendar calendar = connectionService.connectToGoogleCalendar(oauthToken);
            Partner partner = partnerService.findPartnerContainingSlot(slot);

            switch (operation) {
                case INSERT -> insertNewEvent(calendar, partner, slot);
                case MODIFY -> updateOldEvent(calendar, partner, slot);
                case REMOVE -> removeOldEvent(calendar, slot);
            }
        } catch (IOException | GeneralSecurityException | NotFoundException ignored) {}
    }

    public void insertAllEvents(List<Slot> slots, OAuth2AuthenticationToken token) {
        try {
            Calendar calendar = connectionService.connectToGoogleCalendar(token);

            slots.forEach(slot -> {
                Partner partner = partnerService.findPartnerContainingSlot(slot);
                Event event = createEvent(partner, slot);
                event.setId(EVENT_NAME_ADDON + slot.getId());
                try {
                    calendar.events().get("primary", EVENT_NAME_ADDON + slot.getId()).execute();
                    updateOldEvent(calendar, partner, slot);
                } catch (IOException e) {
                    insertNewEvent(calendar, partner, slot);
                }
            });
        } catch (ResponseStatusException | GeneralSecurityException | IOException ignored) {}
    }

    private OAuth2AuthenticationToken checkIfUserIsLoggedByOAuth2(User user) throws NotFoundException {
        return authorizeService.getAuthorizationObject(user.getId());
    }

    private void insertNewEvent(Calendar calendar, Partner partner, Slot slot) {
        try {
            Event event = createEvent(partner, slot);
            event.setId(EVENT_NAME_ADDON + slot.getId());
            calendar.events().insert("primary", event).execute();
        } catch (IOException e) {
            updateOldEvent(calendar, partner, slot);
        }
    }

    private void updateOldEvent(Calendar calendar, Partner partner, Slot slot) {
        try {
            Event event = createEvent(partner, slot);
            calendar.events().update("primary", EVENT_NAME_ADDON + slot.getId(), event).execute();
        } catch (IOException ignore) {}
    }

    private void removeOldEvent(Calendar calendar, Slot slot) {
        try {
            calendar.events().delete("primary", EVENT_NAME_ADDON + slot.getId()).execute();
        } catch (IOException ignore) {}
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
