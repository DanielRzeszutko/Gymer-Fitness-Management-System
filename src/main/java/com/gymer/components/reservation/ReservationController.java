package com.gymer.components.reservation;

import com.gymer.components.common.entity.JsonResponse;
import com.gymer.components.reservation.entity.GuestReservationDetails;
import com.gymer.components.reservation.entity.UserReservationDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/reservation")
public class ReservationController {

    private final ReservationService reservationService;

    @Autowired
    public ReservationController(ReservationService reservationService) {
        this.reservationService = reservationService;
    }

    @PostMapping("/guest")
    @ResponseStatus(HttpStatus.OK)
    public JsonResponse reserveAsGuest(@RequestBody GuestReservationDetails details) {
        return reservationService.updateReservationForGuest(details);
    }

    @PostMapping("/user")
    @ResponseStatus(HttpStatus.OK)
    public JsonResponse reserveAsUser(@RequestBody UserReservationDetails details) {
        return reservationService.updateReservationForUser(details);
    }

}
