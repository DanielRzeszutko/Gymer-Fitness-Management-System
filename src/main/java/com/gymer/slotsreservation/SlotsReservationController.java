package com.gymer.slotsreservation;

import com.gymer.commoncomponents.googlecalendar.CalendarOperation;
import com.gymer.commoncomponents.googlecalendar.GoogleCalendarOperationService;
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

import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
        validateIfSlotIdIsCorrect(details.getSlotId(), slotId);

        Slot slot = reservationService.getSlotFromSlotServiceById(details.getSlotId());
        if (details.isCancel()) {
            if (reservationService.isLessThan24HBeforeVisit(slot)) {
                throw new ResponseStatusException(HttpStatus.NOT_ACCEPTABLE, language.tooLateToDropVisit());
            }
            reservationService.cancelReservationAsGuest(slot, details.getEmail());
            throw new ResponseStatusException(HttpStatus.OK, language.reservationRemoved());
        }
        validateIfSlotIsDeprecated(slot);
        validateIfSlotHasPlace(slot);
        validateEmptyCredentials(details);

        User user = reservationService.createGuestAccount(details);
        validateIfUserAlreadyExistInSlot(user, slot);

        reservationService.reserveUserInSlot(slot, user);
        reservationService.sendGuestVerificationEmail(user.getCredential(), slot);
        throw new ResponseStatusException(HttpStatus.OK, language.pleaseVerifySlotReservation());
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
        validateIfUserLoggedIn(details);

        Slot slot = reservationService.getSlotFromSlotServiceById(details.getSlotId());
        User user = reservationService.getUserFromUserServiceById(details.getUserId());
        if (details.isCancel()) {
            if (reservationService.isLessThan24HBeforeVisit(slot)) {
                throw new ResponseStatusException(HttpStatus.NOT_ACCEPTABLE, language.tooLateToDropVisit());
            }
            reservationService.removeUserFromSlot(slot, user);
            operationService.manipulateWithEvent(slot, user, CalendarOperation.REMOVE);
            throw new ResponseStatusException(HttpStatus.OK, language.reservationRemoved());
        }
        validateIfSlotIsDeprecated(slot);
        validateIfSlotHasPlace(slot);
        validateIfUserAlreadyExistInSlot(user, slot);

        reservationService.reserveUserInSlot(slot, user);
        operationService.manipulateWithEvent(slot, user, CalendarOperation.INSERT);
        throw new ResponseStatusException(HttpStatus.OK, language.successfullyReservedNewSlot());
    }

    private void validateEmptyCredentials(GuestReservationDetails details) {
        String email = details.getEmail();
        String name = details.getFirstName();
        String surname = details.getLastName();
        String phone = details.getPhoneNumber();

        Pattern pattern = Pattern.compile("[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,4}");
        Matcher mat = pattern.matcher(email);
        if (email.length() < 3 || name.length() < 3 || surname.length() < 3 || phone.length() < 9) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, language.fieldsCannotBeEmpty());
        }
        if (!mat.matches() || !phone.matches("^[0-9]*$")) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, language.invalidInput());
        }
    }

    private void validateIfUserLoggedIn(UserReservationDetails details) {
        if (!reservationService.isUserLoggedAsActiveUser(details.getUserId())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, language.signInAsValidUser());
        }
    }

    private void validateIfSlotIdIsCorrect(Long givenSlotId, Long slotId) {
        if (!givenSlotId.equals(slotId)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, language.invalidSlotId());
        }
    }

    private void validateIfUserAlreadyExistInSlot(User user, Slot slot) {
        String email = user.getCredential().getEmail();
        if (reservationService.isUserInSlotByEmail(email, slot)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, language.alreadyReserved());
        }
    }

    private void validateIfSlotHasPlace(Slot slot) {
        if (slot.isPrivate() && slot.getUsers().size() >= slot.getSize()) {
            throw new ResponseStatusException(HttpStatus.NOT_ACCEPTABLE, language.alreadyTakenSlot());
        }
        if (!slot.isPrivate() && slot.getUsers().size() >= slot.getSize()) {
            throw new ResponseStatusException(HttpStatus.NOT_ACCEPTABLE, language.alreadyFullSlot());
        }
    }

    private void validateIfSlotIsDeprecated(Slot slot) {
        if (reservationService.isSlotDeprecated(slot)) {
            throw new ResponseStatusException(HttpStatus.NOT_ACCEPTABLE, language.slotIsDeprecated());
        }
    }

}
