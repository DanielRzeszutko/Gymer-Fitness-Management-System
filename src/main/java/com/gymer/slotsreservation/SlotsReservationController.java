package com.gymer.slotsreservation;

import com.gymer.commoncomponents.googlecalendar.CalendarOperation;
import com.gymer.commoncomponents.googlecalendar.GoogleCalendarOperationService;
import com.gymer.commoncomponents.languagepack.LanguageComponent;
import com.gymer.commonresources.partner.PartnerService;
import com.gymer.commonresources.slot.entity.Slot;
import com.gymer.commonresources.user.entity.User;
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
    private final GoogleCalendarOperationService operationService;
    private final LanguageComponent language;

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
    public void reserveAsGuest(@RequestBody GuestReservationDetails details, @PathVariable Long slotId) {
        if (!details.getSlotId().equals(slotId)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, language.invalidSlotId());
        }

        Slot slot = reservationService.getSlotFromSlotServiceById(details.getSlotId());
        if (details.isCancel()) {
            if (!reservationService.isMoreThan24HBeforeVisit(slot)) {
                throw new ResponseStatusException(HttpStatus.NOT_ACCEPTABLE, language.tooLateToDropVisit());
            }
            reservationService.cancelReservationAsGuest(slot, details.getEmail());
            throw new ResponseStatusException(HttpStatus.OK, language.reservationRemoved());
        }

        if (reservationService.isUserExistByEmail(details.getEmail())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, language.userAlreadyExists());
        }

        User user = reservationService.createGuestAccount(details);
        if (slot.getUsers().contains(user)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, language.alreadyReserved());
        }

        reservationService.reserveUserInSlot(slot, user);
        throw new ResponseStatusException(HttpStatus.OK, language.successfullyReservedNewSlot());
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
            throw new ResponseStatusException(HttpStatus.CONFLICT, language.invalidSlotId());
        }

        if (!reservationService.isUserLoggedAsActiveUser(details.getUserId())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, language.signInAsValidUser());
        }

        Slot slot = reservationService.getSlotFromSlotServiceById(details.getSlotId());
        User user = reservationService.getUserFromUserServiceById(details.getUserId());
        if (details.isCancel()) {
            if (!reservationService.isMoreThan24HBeforeVisit(slot)) {
                throw new ResponseStatusException(HttpStatus.NOT_ACCEPTABLE, language.tooLateToDropVisit());
            }

            reservationService.removeUserFromSlot(slot, user);
            operationService.manipulateWithEvent(slot, CalendarOperation.REMOVE);
            throw new ResponseStatusException(HttpStatus.OK, language.reservationRemoved());
        }

        if (slot.getUsers().contains(user)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, language.alreadyReserved());
        }

        reservationService.reserveUserInSlot(slot, user);
        operationService.manipulateWithEvent(slot, CalendarOperation.INSERT);
        throw new ResponseStatusException(HttpStatus.OK, language.successfullyReservedNewSlot());
    }

}
