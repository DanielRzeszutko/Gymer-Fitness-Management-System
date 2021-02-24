package com.gymer.components.security.changepassword.entity;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PasswordDetails {

    String oldPassword;
    String newPassword;

}
