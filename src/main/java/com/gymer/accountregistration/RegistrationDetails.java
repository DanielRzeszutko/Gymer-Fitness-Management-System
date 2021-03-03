package com.gymer.accountregistration;

import lombok.Data;

@Data
class RegistrationDetails {

    private String email;
    private String password;
    private String confirmPassword;

}
