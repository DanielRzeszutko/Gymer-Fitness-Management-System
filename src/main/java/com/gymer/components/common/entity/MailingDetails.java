package com.gymer.components.common.entity;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class MailingDetails {

    private String mailTo;
    private String subject;
    private String message;

}
