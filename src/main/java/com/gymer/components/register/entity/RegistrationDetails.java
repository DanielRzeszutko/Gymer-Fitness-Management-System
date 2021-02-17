package com.gymer.components.register.entity;

import lombok.Data;

@Data
public class RegistrationDetails {

    private String email;
    private String password;
    private String confirmPassword;

}
