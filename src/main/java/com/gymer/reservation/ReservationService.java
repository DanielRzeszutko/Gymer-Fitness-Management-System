package com.gymer.reservation;

import com.gymer.resources.credential.CredentialService;
import com.gymer.resources.credential.entity.Credential;
import com.gymer.resources.credential.entity.Role;
import com.gymer.resources.slot.SlotService;
import com.gymer.resources.slot.entity.Slot;
import com.gymer.resources.user.UserService;
import com.gymer.resources.user.entity.User;
import com.gymer.common.entity.JsonResponse;
import com.gymer.reservation.entity.GuestReservationDetails;
import com.gymer.reservation.entity.UserReservationDetails;
import com.gymer.security.validation.AccountOwnerValidator;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Date;

@Service
@AllArgsConstructor
class ReservationService {

    private final UserService userService;
    private final SlotService slotService;
    private final CredentialService credentialService;
    private final AccountOwnerValidator accountOwnerValidator;

    /**
     * Service method that returns response in JSON format and adds guest to slot and saves this in database
     * When guests credentials exists they are requested from database in another case new User with blank credentials is created instead
     * When in details field cancel is set to true Guest is removed from the slot
     */
    public JsonResponse updateReservationForGuest(GuestReservationDetails details) {
        Slot slot = slotService.getElementById(details.getSlotId());

        if (details.isCancel()) return cancelReservationAsGuest(slot, details.getEmail());
        if (userService.isUserExistsByEmail(details.getEmail())) return userAlreadyExistsResponse();

        User user = createGuestAccount(details);
        if (slot.getUsers().contains(user)) return alreadyReservedResponse();

        return reserveUserInSlot(slot, user);
    }

    /**
     * Service method that returns response in JSON format and adds user to slot and saves this in database
     * When in details field cancel is set to true User is removed from the slot
     */
    public JsonResponse updateReservationForUser(UserReservationDetails details) {
        Slot slot = slotService.getElementById(details.getSlotId());
        User user = userService.getElementById(details.getUserId());
        if (!accountOwnerValidator.isOwnerLoggedIn(user.getId())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        }

        if (details.isCancel()) return removeUserFromSlot(slot, user);
        if (slot.getUsers().contains(user)) return alreadyReservedResponse();
        return reserveUserInSlot(slot, user);
    }

    private User createGuestAccount(GuestReservationDetails details) {
        Credential credential = credentialService.getCredentialFromEmailPhoneAndRoleOrCreateNewOne(
                details.getEmail(), details.getPhoneNumber(), Role.GUEST
        );
        User user = new User(details.getFirstName(), details.getLastName(), credential);
        userService.updateElement(user);
        return user;
    }

    private JsonResponse cancelReservationAsGuest(Slot slot, String email) {
        User user = slot.getUsers().stream()
                .filter(el -> el.getCredential().getEmail().equals(email))
                .findFirst()
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        return removeUserFromSlot(slot, user);
    }

    private JsonResponse removeUserFromSlot(Slot slot, User user) {
        if (isMoreThan24HBeforeVisit(slot)) {
            slot.getUsers().remove(user);
            slotService.updateElement(slot);
            return JsonResponse.validMessage("Successfully unreserved visit.");
        }

        return JsonResponse.invalidMessage("You can't drop visit now, too late.");
    }

    private boolean isMoreThan24HBeforeVisit(Slot slot) {
        Timestamp timestampNow = new Timestamp(new Date().getTime());
        LocalDateTime now = timestampNow.toLocalDateTime().plusDays(1);
        LocalDateTime visitTime = LocalDateTime.of(slot.getDate().toLocalDate(), slot.getStartTime().toLocalTime());
        return now.isBefore(visitTime);
    }

    private JsonResponse userAlreadyExistsResponse() {
        return JsonResponse.invalidMessage("User with this email already exists.");
    }

    private JsonResponse alreadyReservedResponse() {
        return JsonResponse.invalidMessage("Already reserved this slot.");
    }

    private JsonResponse reserveUserInSlot(Slot slot, User user) {
        slot.getUsers().add(user);
        slotService.updateElement(slot);
        return JsonResponse.validMessage("Successfully reserved this slot.");
    }

}
