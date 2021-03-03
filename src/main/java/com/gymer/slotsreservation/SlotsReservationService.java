package com.gymer.slotsreservation;

import com.gymer.common.crudresources.credential.CredentialService;
import com.gymer.common.crudresources.credential.entity.Credential;
import com.gymer.common.crudresources.credential.entity.Role;
import com.gymer.common.crudresources.slot.SlotService;
import com.gymer.common.crudresources.slot.entity.Slot;
import com.gymer.common.crudresources.user.UserService;
import com.gymer.common.crudresources.user.entity.User;
import com.gymer.security.validation.AccountOwnerValidator;
import com.gymer.slotsreservation.entity.GuestReservationDetails;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Date;

@Service
@AllArgsConstructor
class SlotsReservationService {

    private final UserService userService;
    private final SlotService slotService;
    private final CredentialService credentialService;
    private final AccountOwnerValidator accountOwnerValidator;

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

    public boolean isUserExistByEmail(String email) {
        return userService.isUserExistsByEmail(email);
    }

    public User getUserFromUserServiceById(Long userId) {
        return userService.getElementById(userId);
    }

    public boolean isUserLoggedAsActiveUser(Long userId) {
        return accountOwnerValidator.isOwnerLoggedIn(userId);
    }

    public User createGuestAccount(GuestReservationDetails details) {
        Credential credential = credentialService.getCredentialFromEmailPhoneAndRoleOrCreateNewOne(
                details.getEmail(), details.getPhoneNumber(), Role.GUEST);
        User user = new User(details.getFirstName(), details.getLastName(), credential);
        userService.updateElement(user);
        return user;
    }

    public void cancelReservationAsGuest(Slot slot, String email) {
        User user = slot.getUsers().stream()
                .filter(el -> el.getCredential().getEmail().equals(email))
                .findFirst()
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not exist"));
        removeUserFromSlot(slot, user);
    }

    public void removeUserFromSlot(Slot slot, User user) {
        if (!isMoreThan24HBeforeVisit(slot)) {
            throw new ResponseStatusException(HttpStatus.NOT_ACCEPTABLE, "You can't drop visit now, too late.");
        }
        slot.getUsers().remove(user);
        slotService.updateElement(slot);
    }

    private boolean isMoreThan24HBeforeVisit(Slot slot) {
        Timestamp timestampNow = new Timestamp(new Date().getTime());
        LocalDateTime now = timestampNow.toLocalDateTime().plusDays(1);
        LocalDateTime visitTime = LocalDateTime.of(slot.getDate().toLocalDate(), slot.getStartTime().toLocalTime());
        return now.isBefore(visitTime);
    }

}
