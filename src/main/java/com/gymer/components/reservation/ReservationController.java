package com.gymer.components.reservation;

import com.gymer.components.common.entity.JsonResponse;
import com.gymer.components.reservation.entity.GuestReservationDetails;
import com.gymer.components.reservation.entity.UserReservationDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/reservation/slots/{slotId}")
public class ReservationController {

    private final ReservationService reservationService;

    @Autowired
    public ReservationController(ReservationService reservationService) {
        this.reservationService = reservationService;
    }

    @PostMapping("/guest")
    @ResponseStatus(HttpStatus.OK)
    public JsonResponse reserveAsGuest(@RequestBody GuestReservationDetails details, @PathVariable Long slotId) {
        if (details.getSlotId().equals(slotId)) return reservationService.updateReservationForGuest(details);
        throw new ResponseStatusException(HttpStatus.CONFLICT);
    }

    @PostMapping("/user")
    @ResponseStatus(HttpStatus.OK)
    public JsonResponse reserveAsUser(@RequestBody UserReservationDetails details, @PathVariable Long slotId) {
        if (details.getSlotId().equals(slotId)) return reservationService.updateReservationForUser(details);
        throw new ResponseStatusException(HttpStatus.CONFLICT);
    }

}
