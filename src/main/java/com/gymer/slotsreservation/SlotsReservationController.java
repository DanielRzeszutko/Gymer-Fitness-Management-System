package com.gymer.slotsreservation;

import com.gymer.commoncomponents.languagepack.LanguageComponent;
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

import java.util.List;

@RestController
@AllArgsConstructor
class SlotsReservationController {

    private final SlotsReservationService reservationService;
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
        validateIfSlotIdIsCorrect(details.getSlotId(), slotId);

        Slot slot = reservationService.getSlotFromSlotServiceById(details.getSlotId());
        if (details.isCancel()) {
            if (!reservationService.isMoreThan24HBeforeVisit(slot)) {
                throw new ResponseStatusException(HttpStatus.NOT_ACCEPTABLE, language.tooLateToDropVisit());
            }
            reservationService.cancelReservationAsGuest(slot, details.getEmail());
            throw new ResponseStatusException(HttpStatus.OK, language.reservationRemoved());
        }
        validateIfSlotTaken(slot);

        User user = reservationService.createGuestAccount(details);
        validateIfUserAlreadyExistInSlot(user, slot);

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
        validateIfSlotIdIsCorrect(details.getSlotId(), slotId);

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
            throw new ResponseStatusException(HttpStatus.OK, language.reservationRemoved());
        }

        validateIfSlotTaken(slot);
        validateIfUserAlreadyExistInSlot(user, slot);

        reservationService.reserveUserInSlot(slot, user);
        throw new ResponseStatusException(HttpStatus.OK, language.successfullyReservedNewSlot());
    }

    private void validateIfSlotIdIsCorrect(Long givenSlotId, Long slotId) {
        if (!givenSlotId.equals(slotId)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, language.invalidSlotId());
        }
    }

    private void validateIfUserAlreadyExistInSlot(User user, Slot slot) {
        String email = user.getCredential().getEmail();
        boolean emailExist = slot.getUsers().stream().anyMatch(el -> el.getCredential().getEmail().equals(email));
        if (emailExist) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, language.alreadyReserved());
        }
    }

    private void validateIfSlotTaken(Slot slot) {
        if (slot.isPrivate() && slot.getUsers().size() == 1) {
            throw new ResponseStatusException(HttpStatus.NOT_ACCEPTABLE, language.alreadyTakenSlot());
        }
        if (!slot.isPrivate() && slot.getUsers().size() == 10) {
            throw new ResponseStatusException(HttpStatus.NOT_ACCEPTABLE, language.alreadyFullSlot());
        }
    }

}
