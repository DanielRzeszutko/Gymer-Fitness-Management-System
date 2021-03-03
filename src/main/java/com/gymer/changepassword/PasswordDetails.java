package com.gymer.changepassword;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
class PasswordDetails {

    private String oldPassword;
    private String newPassword;

}
