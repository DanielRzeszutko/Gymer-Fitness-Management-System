package com.gymer.mailandsmsnotifications;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gymer.commoncomponents.languagepack.LanguageComponent;
import com.gymer.commonresources.slot.entity.Slot;
import com.gymer.commonresources.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

@Service
@RequiredArgsConstructor
class SmsNotificationService {

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final Environment environment;
    private final LanguageComponent language;

    public void sendNotification(User user, Slot slot) throws IOException, InterruptedException {
        String message = language.getSmsNotificationWhenSlotStartsInAnHour(user, slot);
        SmsDetails details = new SmsDetails("", "SMSto", message, user.getCredential().getPhoneNumber());
        String authKey = environment.getProperty("sms.secret.password");
        authKey = authKey == null ? "" : authKey;

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://api.sms.to/sms/send"))
                .header("content-type", "application/json")
                .header("authorization", "Bearer " + authKey)
                .method("POST", HttpRequest.BodyPublishers.ofString(objectMapper.writeValueAsString(details)))
                .build();
        HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
    }

}
