package com.gymer.databasecleaner;

import com.gymer.commonresources.slot.SlotService;
import com.gymer.commonresources.slot.entity.Slot;
import com.gymer.commonresources.user.UserService;
import com.gymer.commonresources.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
@RequiredArgsConstructor
class UnverifiedReservationService {

    private final SlotService slotService;
    private final UserService userService;

    @Transactional
    @Scheduled(fixedRate = 10000)
    public void deleteUnverifiedReservation() {
        Iterable<User> users = userService.findAllGuestOlderThan10MinutesYoungerThan15Minutes();
        users.forEach(user -> {
            Iterable<Slot> slots = slotService.findAllSlotsForGuest(user);
            slots.forEach(slot -> removeUserFromSlot(slot, user));
        });
    }

    public void removeUserFromSlot(Slot slot, User user) {
        slot.getUsers().remove(user);
        slotService.updateElement(slot);
    }

}
