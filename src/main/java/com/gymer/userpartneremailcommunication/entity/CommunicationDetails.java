package com.gymer.userpartneremailcommunication.entity;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CommunicationDetails {

    private Long partnerId;
    private Long userId;
    private String message;

}
