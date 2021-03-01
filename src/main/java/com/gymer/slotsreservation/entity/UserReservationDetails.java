package com.gymer.slotsreservation.entity;

import lombok.Data;

@Data
public class UserReservationDetails {

    private Long userId;
    private Long slotId;
    private boolean cancel;

}
