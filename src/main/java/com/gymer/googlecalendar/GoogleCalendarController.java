package com.gymer.googlecalendar;

import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.CalendarScopes;
import com.google.api.services.calendar.model.CalendarList;
import com.google.api.services.calendar.model.CalendarListEntry;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.io.InputStream;
import java.security.GeneralSecurityException;
import java.util.Collections;
import java.util.List;

@RestController
public class GoogleCalendarController {

    @GetMapping("/api/google-active")
    public void getGoogleAccount() throws IOException, GeneralSecurityException {
        HttpTransport httpTransport = GoogleNetHttpTransport.newTrustedTransport();
        JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();
        InputStream is = GoogleCalendarController.class.getClassLoader().getResourceAsStream("credentials.json");
        GoogleCredential credential = GoogleCredential.fromStream(is)
                .createScoped(Collections.singleton(CalendarScopes.CALENDAR));

        Calendar service = new Calendar.Builder(httpTransport, jsonFactory, credential)
                .setApplicationName("applicationName").build();

        String pageToken = null;
        do {
            CalendarList calendarList = service.calendarList().list().setPageToken(pageToken).execute();
            System.out.println(calendarList);
            List<CalendarListEntry> items = calendarList.getItems();

            for (CalendarListEntry calendarListEntry : items) {
                System.out.println(calendarListEntry.getSummary());
            }
            pageToken = calendarList.getNextPageToken();
        } while (pageToken != null);
    }

}
