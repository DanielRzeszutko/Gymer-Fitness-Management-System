package com.gymer.reservation.entity;

import lombok.Data;

@Data
public class GuestReservationDetails {

    private String email;
    private String phoneNumber;
    private String firstName;
    private String lastName;
    private Long slotId;
    private boolean cancel;

}
