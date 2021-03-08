package com.gymer.mailandsmsnotifications;

import com.gymer.commonresources.slot.SlotService;
import com.gymer.commonresources.slot.entity.Slot;
import com.gymer.commonresources.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

@Service
@RequiredArgsConstructor
class HourToSlotNotificationService {

    private final SlotService slotService;
    private final MailNotificationService mailNotificationService;
    private final SmsNotificationService smsNotificationService;

    // Method runs every 60 seconds
    @Scheduled(fixedRate = 60000)
    public void sendNotificationEveryMinute() {
        Iterable<Slot> slots = slotService.findAllSlotsTodayStartingInAnHour();
        slots.forEach(slot -> {
            List<User> users = slot.getUsers();
            users.forEach(user -> {
                mailNotificationService.sendNotification(user, slot);
                try {
                    smsNotificationService.sendNotification(user, slot);
                } catch (IOException | InterruptedException e) {
                    e.printStackTrace();
                }
            });
        });
    }

}
