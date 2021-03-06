package com.gymer.slotsreservation;

import com.gymer.commoncomponents.accountvalidator.AccountOwnerValidator;
import com.gymer.commonresources.credential.entity.Credential;
import com.gymer.commonresources.credential.entity.Role;
import com.gymer.commonresources.slot.SlotService;
import com.gymer.commonresources.slot.entity.Slot;
import com.gymer.commonresources.user.UserService;
import com.gymer.commonresources.user.entity.User;
import lombok.AllArgsConstructor;
import net.bytebuddy.utility.RandomString;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
class SlotsReservationService {

    private final UserService userService;
    private final SlotService slotService;
    private final AccountOwnerValidator accountOwnerValidator;
    private final GuestVerifyEmailService emailService;

    /**
     * Service method that returns response in JSON format and adds guest to slot and saves this in database
     * When guests credentials exists they are requested from database in another case new User with blank credentials is created instead
     * When in details field cancel is set to true Guest is removed from the slot
     *
     * @param slot - ...
     * @param user - ...
     * @return JsonResponse - object with message and valid status if data is filled successfully or
     * message and invalid status if any error occurs during reading the text files.
     */
    public void reserveUserInSlot(Slot slot, User user) {
        slot.getUsers().add(user);
        slotService.updateElement(slot);
    }

    public Slot getSlotFromSlotServiceById(Long slotId) {
        return slotService.getElementById(slotId);
    }

    public User getUserFromUserServiceById(Long userId) {
        return userService.getElementById(userId);
    }

    public boolean isUserLoggedAsActiveUser(Long userId) {
        return accountOwnerValidator.isOwnerLoggedIn(userId);
    }

    public User createGuestAccount(GuestReservationDetails details) {
        Credential credential = new Credential(details.getEmail(), null, details.getPhoneNumber(),
                Role.GUEST, false, new Timestamp(new java.util.Date().getTime()));

        credential.setVerificationCode(RandomString.make(10));
        User user = new User(details.getFirstName(), details.getLastName(), credential);
        userService.updateElement(user);
        return user;
    }

    public void cancelReservationAsGuest(Slot slot, String email) {
        List<User> users = slot.getUsers().stream()
                .filter(el -> el.getCredential().getEmail().equals(email))
                .collect(Collectors.toList());
        users.forEach(user -> removeUserFromSlot(slot, user));
    }

    public void removeUserFromSlot(Slot slot, User user) {
        slot.getUsers().remove(user);
        slotService.updateElement(slot);
    }

    public boolean isUserInSlotByEmail(String email, Slot slot) {
        return slot.getUsers().stream().anyMatch(el -> el.getCredential().getEmail().equals(email));
    }

    public boolean isLessThan24HBeforeVisit(Slot slot) {
        return isTooLateFromNow(slot, 24L);
    }

    public boolean isSlotDeprecated(Slot slot) {
        return isTooLateFromNow(slot, 1L);
    }

    public void sendGuestVerificationEmail(Credential credential, Slot slot) {
        emailService.sendGuestVerificationEmail(credential, slot);
    }

    private boolean isTooLateFromNow(Slot slot, Long howManyHoursBefore) {
        Timestamp timestampNow = new Timestamp(new Date().getTime());
        LocalDateTime now = timestampNow.toLocalDateTime().plusHours(howManyHoursBefore);
        LocalDateTime visitTime = LocalDateTime.of(slot.getDate().toLocalDate(), slot.getStartTime().toLocalTime());
        return visitTime.isBefore(now);
    }

}
