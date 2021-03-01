package com.gymer.slotsreservation;

import com.gymer.common.crudresources.credential.CredentialService;
import com.gymer.common.crudresources.credential.entity.Credential;
import com.gymer.common.crudresources.credential.entity.Role;
import com.gymer.common.crudresources.slot.SlotService;
import com.gymer.common.crudresources.slot.entity.Slot;
import com.gymer.common.crudresources.user.UserService;
import com.gymer.common.crudresources.user.entity.User;
import com.gymer.common.entity.JsonResponse;
import com.gymer.security.validation.AccountOwnerValidator;
import com.gymer.slotsreservation.entity.GuestReservationDetails;
import com.gymer.slotsreservation.entity.UserReservationDetails;
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
     * @param details - Object with userId, slotId and boolean deciding if record need to be removed.
     *                Custom object holding information only for reservation feature.
     * @return JsonResponse - object with message and valid status if data is filled successfully or
     * message and invalid status if any error occurs during reading the text files.
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
     * Service method that returns response in JSON format and adds user to slot and saves this in database.
     * When in details field cancel is set to true User is removed from the slot.
     *
     * @param details - Object with userId, slotId and boolean deciding if record need to be removed.
     *                Custom object holding information only for reservation feature.
     * @return JsonResponse - object with message and valid status if data is filled successfully or
     * message and invalid status if any error occurs during reading the text files.
     */
    public JsonResponse updateReservationForUser(UserReservationDetails details) {
        Slot slot = slotService.getElementById(details.getSlotId());
        User user = userService.getElementById(details.getUserId());

        if (!accountOwnerValidator.isOwnerLoggedIn(user.getId())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Sign in as valid user");
        }

        if (details.isCancel()) return removeUserFromSlot(slot, user);
        if (slot.getUsers().contains(user)) return alreadyReservedResponse();
        return reserveUserInSlot(slot, user);
    }

    private User createGuestAccount(GuestReservationDetails details) {
        Credential credential = credentialService.getCredentialFromEmailPhoneAndRoleOrCreateNewOne(
                details.getEmail(), details.getPhoneNumber(), Role.GUEST);
        User user = new User(details.getFirstName(), details.getLastName(), credential);
        userService.updateElement(user);
        return user;
    }

    private JsonResponse cancelReservationAsGuest(Slot slot, String email) {
        User user = slot.getUsers().stream()
                .filter(el -> el.getCredential().getEmail().equals(email))
                .findFirst()
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not exist"));
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
