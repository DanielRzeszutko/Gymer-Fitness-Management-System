package com.gymer.slotsreservation;

import lombok.Data;

@Data
class UserReservationDetails {

    private Long userId;
    private Long slotId;
    private boolean cancel;

}
