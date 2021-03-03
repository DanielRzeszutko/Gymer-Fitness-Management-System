package com.gymer.userpartnercommunication;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
class CommunicationDetails {

    private Long partnerId;
    private Long userId;
    private String message;

}
