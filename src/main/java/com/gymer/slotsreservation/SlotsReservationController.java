package com.gymer.slotsreservation;

import com.gymer.common.entity.JsonResponse;
import com.gymer.slotsreservation.entity.GuestReservationDetails;
import com.gymer.slotsreservation.entity.UserReservationDetails;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@AllArgsConstructor
class SlotsReservationController {

    private final SlotsReservationService reservationService;

    /**
     * Method that returns response in JSON format. For guests only. First checking if there is not conflict with
     * provided slotId and @RequestBody slotId and next updating/adding reservation or throwing
     * CONFLICT status in response.
     * @param details - Object with userId, slotId and boolean deciding if record need to be removed.
     *                Custom object holding information only for reservation feature.
     * @param slotId - Id of the slot which to user want to reserve. Must be the same as ID in body object.
     * @return JsonResponse - object with message and valid status if data is filled successfully or
     * message and invalid status if any error occurs during reading the text files.
     */
    @PostMapping("/api/slotuser/{slotId}/reservation/guest")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasRole('ADMIN') or @accountOwnerValidator.isGuest()")
    public JsonResponse reserveAsGuest(@RequestBody GuestReservationDetails details, @PathVariable Long slotId) {
        if (details.getSlotId().equals(slotId)) return reservationService.updateReservationForGuest(details);
        throw new ResponseStatusException(HttpStatus.CONFLICT, "Invalid slot id");
    }

    /**
     * Method that returns response in JSON format. For logged user only. First checking if there is not conflict with
     *      * provided slotId and @RequestBody slotId and next updating/adding reservation or throwing
     *      * CONFLICT status in response.
     * @param details - Object with userId, slotId and boolean deciding if record need to be removed.
     *                Custom object holding information only for reservation feature.
     * @param slotId - Id of the slot which to user want to reserve. Must be the same as ID in body object.
     * @return JsonResponse - object with message and valid status if data is filled successfully or
     * message and invalid status if any error occurs during reading the text files.
     */
    @PostMapping("/api/slotuser/{slotId}/reservation/user")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
    public JsonResponse reserveAsUser(@RequestBody UserReservationDetails details, @PathVariable Long slotId) {
        if (details.getSlotId().equals(slotId)) return reservationService.updateReservationForUser(details);
        throw new ResponseStatusException(HttpStatus.CONFLICT, "Invalid slot id");
    }

}
