package com.gymer.security.entity;

import lombok.Data;

@Data
public class RegisterCredentials {

    private String email;
    private String password;
    private String confirmPassword;

}
