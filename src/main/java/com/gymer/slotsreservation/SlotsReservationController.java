package com.gymer.slotsreservation;

import com.gymer.common.crudresources.slot.entity.Slot;
import com.gymer.common.crudresources.user.entity.User;
import com.gymer.slotsreservation.entity.GuestReservationDetails;
import com.gymer.slotsreservation.entity.UserReservationDetails;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@AllArgsConstructor
class SlotsReservationController {

    private final SlotsReservationService reservationService;

    /**
     * Method that returns response in JSON format. For guests only. First checking if there is not conflict with
     * provided slotId and @RequestBody slotId and next updating/adding reservation or throwing
     * CONFLICT status in response.
     *
     * @param details - Object with userId, slotId and boolean deciding if record need to be removed.
     *                Custom object holding information only for reservation feature.
     * @param slotId  - Id of the slot which to user want to reserve. Must be the same as ID in body object.
     * @return JsonResponse - object with message and valid status if data is filled successfully or
     * message and invalid status if any error occurs during reading the text files.
     */
    @PostMapping("/api/slotuser/{slotId}/reservation/guest")
    @PreAuthorize("hasRole('ADMIN') or @accountOwnerValidator.isGuest()")
    public void reserveAsGuest(@RequestBody GuestReservationDetails details, @PathVariable Long slotId) {
        if (!details.getSlotId().equals(slotId)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Invalid slot id.");
        }

        Slot slot = reservationService.getSlotFromSlotServiceById(details.getSlotId());
        if (details.isCancel()) {
            reservationService.cancelReservationAsGuest(slot, details.getEmail());
            throw new ResponseStatusException(HttpStatus.OK, "Successfully removed reservation.");
        }

        if (reservationService.isUserExistByEmail(details.getEmail())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "User with this email already exists.");
        }

        User user = reservationService.createGuestAccount(details);
        if (slot.getUsers().contains(user)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Already reserved this slot.");
        }

        reservationService.reserveUserInSlot(slot, user);
        throw new ResponseStatusException(HttpStatus.OK, "Successfully added reservation details.");
    }

    /**
     * Method that returns response in JSON format. For logged user only. First checking if there is not conflict with
     * * provided slotId and @RequestBody slotId and next updating/adding reservation or throwing
     * * CONFLICT status in response.
     *
     * @param details - Object with userId, slotId and boolean deciding if record need to be removed.
     *                Custom object holding information only for reservation feature.
     * @param slotId  - Id of the slot which to user want to reserve. Must be the same as ID in body object.
     * @return JsonResponse - object with message and valid status if data is filled successfully or
     * message and invalid status if any error occurs during reading the text files.
     */
    @PostMapping("/api/slotuser/{slotId}/reservation/user")
    @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
    public void reserveAsUser(@RequestBody UserReservationDetails details, @PathVariable Long slotId) {
        if (!details.getSlotId().equals(slotId)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Invalid slot id.");
        }

        if (!reservationService.isUserLoggedAsActiveUser(details.getUserId())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Sign in as valid user");
        }

        Slot slot = reservationService.getSlotFromSlotServiceById(details.getSlotId());
        User user = reservationService.getUserFromUserServiceById(details.getUserId());
        if (details.isCancel()) {
            reservationService.removeUserFromSlot(slot, user);
            throw new ResponseStatusException(HttpStatus.OK, "Successfully removed reservation.");
        }

        if (slot.getUsers().contains(user)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Already reserved this slot.");
        }

        reservationService.reserveUserInSlot(slot, user);
        throw new ResponseStatusException(HttpStatus.OK, "Successfully added reservation details.");
    }

}
