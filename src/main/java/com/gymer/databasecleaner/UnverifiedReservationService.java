package com.gymer.databasecleaner;

import com.gymer.commonresources.credential.CredentialService;
import com.gymer.commonresources.credential.entity.Credential;
import com.gymer.commonresources.slot.SlotService;
import com.gymer.commonresources.slot.entity.Slot;
import com.gymer.commonresources.user.UserService;
import com.gymer.commonresources.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
class UnverifiedReservationService {

    private final SlotService slotService;
    private final CredentialService credentialService;
    private final UserService userService;

    @Scheduled(fixedRate = 10000)
    public void deleteUnverifiedReservation() {
        System.out.println("START");

        Iterable<User> users = userService.findAllGuestOlderThan10Minutes();
        System.out.println(users);

        //TODO Not working too.
//        User juzer = userService.findbyuserid(24L);
//        Iterable<Slot> slots = slotService.findAllSlotsForGuest(juzer);
//        System.out.println(slots);

        //TODO not working. LAZY INITIALIZATION BUG
//        users.forEach(user -> {
//            Iterable<Slot> slots = slotService.findAllSlotsForGuest(user);
//            System.out.println(slots);
//        });


    }

    public void removeUserFromSlot(Slot slot, User user) {
        slot.getUsers().remove(user);
        slotService.updateElement(slot);
    }

}
