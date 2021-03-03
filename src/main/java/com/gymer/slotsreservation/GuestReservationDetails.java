package com.gymer.slotsreservation;

import lombok.Data;

@Data
class GuestReservationDetails {

    private String email;
    private String phoneNumber;
    private String firstName;
    private String lastName;
    private Long slotId;
    private boolean cancel;

}
