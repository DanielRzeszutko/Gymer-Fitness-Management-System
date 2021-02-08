package com.gymer.api.credential.entity;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CredentialDTO {

    private Long id;
    private String email;
    private String password;
    private String phoneNumber;
    private boolean active;

}
