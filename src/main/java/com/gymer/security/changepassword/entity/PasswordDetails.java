package com.gymer.security.changepassword.entity;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PasswordDetails {

    private String oldPassword;
    private String newPassword;

}
