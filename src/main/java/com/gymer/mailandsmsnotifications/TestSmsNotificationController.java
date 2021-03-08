package com.gymer.mailandsmsnotifications;

import com.gymer.commonresources.credential.entity.Credential;
import com.gymer.commonresources.credential.entity.Role;
import com.gymer.commonresources.slot.entity.Slot;
import com.gymer.commonresources.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Collections;
import java.util.Random;

@RestController
@RequiredArgsConstructor
class TestSmsNotificationController {

    private final SmsNotificationService smsNotificationService;

    @GetMapping("/api/sms")
    public void sendSmsNotification(@RequestParam(name = "to") String phoneNumber,
                                    @RequestParam(name = "content") String content) throws IOException, InterruptedException {
        Timestamp time = new Timestamp(System.currentTimeMillis());
        Credential credential = new Credential(null, null, phoneNumber, Role.USER, true, time);
        User user = getUser(credential);
        Slot slot = getSlot(content);
        user.getCredential().setPhoneNumber(phoneNumber);
        smsNotificationService.sendNotification(user, slot);
    }

    private User getUser(Credential credential) {
        return new User("Kamil", "Nowak", credential);
    }

    private Slot getSlot(String content) {
        Date dateNow = new Date(new Timestamp(System.currentTimeMillis()).getTime());
        int startHour = new Random().nextInt(12) + 6;
        int endHour = startHour + 1;
        String startHourString = startHour < 10 ? "0" + startHour : Integer.toString(startHour);
        String endHourString = endHour < 10 ? "0" + endHour : Integer.toString(endHour);
        boolean isPrivate = startHour > 12;
        Integer size = isPrivate ? 1 : 10;
        return new Slot(content, dateNow, Time.valueOf(startHourString + ":00:00"),
                Time.valueOf(endHourString + ":00:00"), Collections.emptyList(), null, "Full body workout", isPrivate, size);
    }

}
