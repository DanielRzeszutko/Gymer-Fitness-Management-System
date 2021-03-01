package com.gymer.reservation;

import com.gymer.common.entity.JsonResponse;
import com.gymer.reservation.entity.GuestReservationDetails;
import com.gymer.reservation.entity.UserReservationDetails;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@AllArgsConstructor
public class ReservationController {

    private final ReservationService reservationService;

    @PostMapping("/api/slotuser/{slotId}/reservation/guest")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasRole('ADMIN') or @accountOwnerValidator.isGuest()")
    public JsonResponse reserveAsGuest(@RequestBody GuestReservationDetails details, @PathVariable Long slotId) {
        if (details.getSlotId().equals(slotId)) return reservationService.updateReservationForGuest(details);
        throw new ResponseStatusException(HttpStatus.CONFLICT, "Invalid slot id");
    }

    @PostMapping("/api/slotuser/{slotId}/reservation/user")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
    public JsonResponse reserveAsUser(@RequestBody UserReservationDetails details, @PathVariable Long slotId) {
        if (details.getSlotId().equals(slotId)) return reservationService.updateReservationForUser(details);
        throw new ResponseStatusException(HttpStatus.CONFLICT, "Invalid slot id");
    }

}
