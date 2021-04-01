package com.gymer.databasecleaner;

import com.gymer.commonresources.credential.CredentialService;
import com.gymer.commonresources.credential.entity.Credential;
import com.gymer.commonresources.slot.SlotService;
import com.gymer.commonresources.slot.entity.Slot;
import com.gymer.commonresources.user.UserService;
import com.gymer.commonresources.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
class UnverifiedReservationService {

    private final SlotService slotService;
    private final CredentialService credentialService;
    private final UserService userService;

//    @Scheduled(fixedRate = 60000)
    public void deleteUnverifiedReservation() {
        System.out.println("START");

        Iterable<Credential> credentials = credentialService.getGuestCredentialsOlderThan10Minutes();

        //TODO not working. LAZY INITIALIZATION BUG

        for (Credential credential : credentials) {
            User user = userService.getByCredentials(credential);
            Iterable<Slot> slots = slotService.findAllSlotsForGuest(user);
            for (Slot slot : slots) {
                List<User> users = slot.getUsers();
                users.remove(user);
                slot.setUsers(users);

                slotService.updateElement(slot);
            }
        }

    }

}
